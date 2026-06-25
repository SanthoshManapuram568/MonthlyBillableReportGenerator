package com.srm.billable.outlook;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.srm.billable.dto.DraftRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

/**
 * Service for creating Outlook drafts using COM automation via JACOB.
 */
@Slf4j
public class OutlookService {

    private static final int OUTLOOK_MAIL_ITEM = 0;

    private boolean outlookAvailable = false;

    /**
     * Checks if Microsoft Outlook is installed and available.
     */
    public void checkOutlookStatus() {
        try {
            JacobNativeLoader.configureJacobDllPath();
            ComThread.InitSTA();

            Dispatch outlookApp = new ActiveXComponent("Outlook.Application").getObject();
            if (outlookApp != null) {
                outlookAvailable = true;
                log.info("Microsoft Outlook is installed and ready");
                Dispatch.call(outlookApp, "Quit");
            }
        } catch (Throwable ex) {
            outlookAvailable = false;
            log.warn("Outlook integration unavailable. Ensure {} is available for this process.",
                JacobNativeLoader.expectedDllName());
            log.warn("Resolved jacob.dll.path: {}", System.getProperty("jacob.dll.path", "<not set>"));
            log.warn("DLL search diagnostics: {}", JacobNativeLoader.searchDiagnostics());
            log.warn("Outlook COM initialization failed: {}", ex.toString());
            log.debug("Outlook/JACOB initialization error details", ex);
        } finally {
            try {
                ComThread.Release();
            } catch (Throwable ignored) {
                // No-op: release can fail when COM thread was never initialized.
            }
        }
    }

    public boolean isOutlookAvailable() {
        return outlookAvailable;
    }

    /**
     * Creates an Outlook draft with the given request data and displays it.
     */
    public void createDraft(DraftRequest request) {
        // Re-check once at request time in case Outlook started after app boot.
        if (!outlookAvailable) {
            checkOutlookStatus();
        }

        if (!outlookAvailable) {
            throw new RuntimeException(
                    "Outlook COM automation is unavailable. Ensure Outlook (classic) is installed and turn OFF 'New Outlook'. " +
                    "Also ensure jacob native DLL is loadable (same bitness as Java: " +
                    System.getProperty("os.arch", "unknown") + ")."
            );
        }

        ComThread.InitSTA();
        try {
            log.info("Creating Outlook draft for: {}", request.getRecipient());

            Dispatch outlookApp = new ActiveXComponent("Outlook.Application").getObject();
            // Initialize MAPI namespace to ensure session setup before item creation.
            Dispatch.call(outlookApp, "GetNamespace", "MAPI").toDispatch();

            Dispatch mailItem = Dispatch.call(outlookApp, "CreateItem", OUTLOOK_MAIL_ITEM).toDispatch();
            Dispatch.put(mailItem, "To", request.getRecipient());

            if (request.getCc() != null && !request.getCc().isEmpty()) {
                Dispatch.put(mailItem, "CC", request.getCc());
            }

            Dispatch.put(mailItem, "Subject", request.getSubject());
            Dispatch.put(mailItem, "HTMLBody", request.getHtmlBody());

            attachFiles(mailItem, request.getAttachments());
            Dispatch.call(mailItem, "Display", false);

            log.info("Outlook draft displayed successfully");
            cleanupTempFiles(request.getAttachments());
        } catch (Exception ex) {
            log.error("Failed to create Outlook draft", ex);
            throw new RuntimeException("Failed to create Outlook draft: " + ex.getMessage(), ex);
        } finally {
            ComThread.Release();
        }
    }

    private void attachFiles(Dispatch mailItem, List<File> files) throws Exception {
        Dispatch attachments = Dispatch.get(mailItem, "Attachments").toDispatch();

        for (File file : files) {
            int retries = 3;
            while (retries > 0) {
                try {
                    log.info("Attaching: {}", file.getName());
                    Dispatch.call(attachments, "Add", file.getAbsolutePath(), 1, 1, file.getName());
                    break;
                } catch (Exception ex) {
                    retries--;
                    if (retries == 0) {
                        log.error("Failed to attach file after 3 retries: {}", file.getName());
                        throw ex;
                    }

                    log.warn("Attachment failed, retrying ({} attempts left)...", retries);
                    Thread.sleep(500);
                }
            }
        }

        log.info("All {} files attached successfully", files.size());
    }

    private void cleanupTempFiles(List<File> files) {
        for (File file : files) {
            try {
                if (file.delete()) {
                    log.debug("Deleted temporary file: {}", file.getAbsolutePath());
                }
            } catch (Exception ex) {
                log.warn("Failed to delete temp file: {}", file.getAbsolutePath(), ex);
            }
        }
    }
}
