package com.device.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import com.device.dto.DevicePatchRequest;
import com.device.dto.DeviceRequest;
import com.device.enums.DeviceStateEnum;
import com.device.model.Device;
import com.device.repository.DeviceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DeviceControllerIT {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private DeviceRepository repository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Device device;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        repository.deleteAll();
        device = Device.builder()
                .name("iPhone 15")
                .brand("Apple")
                .state(DeviceStateEnum.AVAILABLE)
                .build();
        device = repository.save(device);
    }

    @Test
    void create_shouldReturn201() throws Exception {
        DeviceRequest request = new DeviceRequest("Galaxy S24", "Samsung", null);

        mockMvc.perform(post("/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Galaxy S24")))
                .andExpect(jsonPath("$.brand", is("Samsung")))
                .andExpect(jsonPath("$.state", is("AVAILABLE")));
    }

    @Test
    void findAll_shouldReturnPage() throws Exception {
        mockMvc.perform(get("/devices")
                .param("brand", "Apple")
                .param("state", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("iPhone 15")));
    }

    @Test
    void findById_shouldReturnDevice() throws Exception {
        mockMvc.perform(get("/devices/{id}", device.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(device.getId().intValue())))
                .andExpect(jsonPath("$.name", is("iPhone 15")));
    }

    @Test
    void findById_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(get("/devices/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void partialUpdate_shouldUpdateFields() throws Exception {
        DevicePatchRequest patchRequest = new DevicePatchRequest("iPhone 15 Pro", null, null);

        mockMvc.perform(patch("/devices/{id}", device.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("iPhone 15 Pro")))
                .andExpect(jsonPath("$.brand", is("Apple")));
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/devices/{id}", device.getId()))
                .andExpect(status().isNoContent());

        Device updatedDevice = repository.findById(device.getId()).orElseThrow();
        assert(updatedDevice.getState() == DeviceStateEnum.INACTIVE);
    }

    @Test
    void create_shouldReturn400_whenInvalidRequest() throws Exception {
        DeviceRequest request = new DeviceRequest("", "", null); // Blank name and brand

        mockMvc.perform(post("/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(2)));
    }
}
