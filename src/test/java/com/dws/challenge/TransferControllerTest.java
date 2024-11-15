package com.dws.challenge;

import com.dws.challenge.service.TransferService;
import com.dws.challenge.web.TransferController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class TransferControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private TransferService mockTransferService;

    private TransferController transferController;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = webAppContextSetup(this.webApplicationContext).build();
        transferController = new TransferController(mockTransferService);
    }

    @Test
    void testTransferSuccess() throws Exception {
        doNothing().when(mockTransferService).processTransfer(anyString(), anyString(), any(BigDecimal.class));

        mockMvc.perform(post("/v1/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountFrom\":\"account1\",\"accountTo\":\"account2\",\"amount\":100.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transfer of 100.00 from account account1 to account account2 completed successfully"));

        // Verify that the service method was called exactly once
        verify(mockTransferService, times(1)).processTransfer("account1", "account2", new BigDecimal("100.00"));
    }

    @Test
    void testTransferRequestValidationFailure() throws Exception {
        String invalidRequest = "{\"accountFrom\":\"\",\"accountTo\":\"account2\",\"amount\":100.00}";

        mockMvc.perform(post("/v1/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());

        // Verify that the service method was never called
        verify(mockTransferService, never()).processTransfer(anyString(), anyString(), any(BigDecimal.class));
    }

    @Test
    void testTransferMissingAmount() throws Exception {
        String invalidRequest = "{\"accountFrom\":\"account1\",\"accountTo\":\"account2\"}";

        mockMvc.perform(post("/v1/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());

        // Verify that the service method was never called
        verify(mockTransferService, never()).processTransfer(anyString(), anyString(), any(BigDecimal.class));
    }

    @Test
    void testTransferInvalidAmount() throws Exception {
        String invalidRequest = "{\"accountFrom\":\"account1\",\"accountTo\":\"account2\",\"amount\":-100.00}";

        mockMvc.perform(post("/v1/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());

        // Verify that the service method was never called
        verify(mockTransferService, never()).processTransfer(anyString(), anyString(), any(BigDecimal.class));
    }
}
