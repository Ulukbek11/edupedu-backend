// package com.edupedu.app;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.edupedu.app.request.AuthenticationRequest;
// import com.edupedu.app.request.RegistrationRequest;
// import com.edupedu.app.model.enums.Role;

// @SpringBootTest
// @AutoConfigureMockMvc
// public class APITest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private ObjectMapper objectMapper;

//     @Test
//     public void testFullLifecycle() throws Exception {
//         String email = "test_user@example.com";
//         String password = "password123";

//         // 1. Register
//         RegistrationRequest regRequest = new RegistrationRequest(
//             email, password, "Test", "User", Role.ROLE_ADMIN
//         );
//         mockMvc.perform(post("/api/v1/register")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(regRequest)))
//                 .andExpect(status().isOk());

//         // 2. Login
//         AuthenticationRequest loginRequest = new AuthenticationRequest(email, password);
//         mockMvc.perform(post("/api/v1/auth/login")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(loginRequest)))
//                 .andExpect(status().isOk());
//     }
// }
