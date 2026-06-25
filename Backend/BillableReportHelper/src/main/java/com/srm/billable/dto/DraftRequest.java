package com.srm.billable.dto;

import lombok.Data;
import java.io.File;
import java.util.List;

/**
 * Request to create an Outlook draft.
 * Populated from multipart/form-data.
 */
@Data
public class DraftRequest {

    private String recipient;      // Required
    private String cc;             // Optional
    private String subject;        // Required
    private String htmlBody;       // Required
    private List<File> attachments; // Required, at least 1
}
