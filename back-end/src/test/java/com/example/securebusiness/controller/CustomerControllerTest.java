package com.example.securebusiness.controller;

import com.example.securebusiness.dto.UserDTO;
import com.example.securebusiness.filter.CustomAuthorizationFilter;
import com.example.securebusiness.model.Customer;
import com.example.securebusiness.model.Stats;
import com.example.securebusiness.repository.RoleRepository;
import com.example.securebusiness.service.CustomerService;
import com.example.securebusiness.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CustomerService customerService;
    @MockBean
    private CustomAuthorizationFilter customAuthorizationFilter;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private UserService userService;
    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    @WithMockUser(username = "Alex")
    public void testGetAllCustomers() throws Exception {
        // Mock customer data
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setFirstName("mossaab");
        userDTO.setLastName("zegaoui");
        userDTO.setEmail("mossaab@gmail.com");
        userDTO.setRoleName("ROLE_USER");
        userDTO.setPermission("READ:USER, READ:CUSTOMER");
        // Mock customer service method
        when(customerService.filterCustomers(0, 10, "")).thenReturn(new PageImpl<>(Collections.singletonList(customer)));
        when(userService.getUserDtoByEmail("mossaab@gmail.com")).thenReturn(userDTO);

        // Perform GET request to /api/v1/customers
        ResultActions resultActions = mockMvc.perform(get("/api/v1/customers")
                        .with(user("Alex"))
                        .contentType(MediaType.APPLICATION_JSON));
        MvcResult result = resultActions.andReturn();

        assertEquals(200, resultActions.andReturn().getResponse().getStatus());
    }
}