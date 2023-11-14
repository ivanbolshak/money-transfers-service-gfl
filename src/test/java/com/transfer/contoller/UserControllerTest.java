package com.transfer.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.model.dto.UserDto;
import com.transfer.repository.AccountRepository;
import com.transfer.repository.TransactionRepository;
import com.transfer.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void addUser_success() throws Exception {

        long countUsersBeforeCall = userRepository.count();

        UserDto userDto = UserDto.builder()
                .firstName("UserOOO")
                .lastName("UserOOO")
                .email("userOOO@gmail.com")
                .uuid(UUID.fromString("333284c9-b0dc-4892-aa15-92ebf91601f7"))
                .build();

        this.mockMvc.perform(post("/rest/api/v1/user")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("first-name").value(userDto.getFirstName()))
                .andExpect(jsonPath("last-name").value(userDto.getLastName()))
                .andExpect(jsonPath("email").value(userDto.getEmail()))
                .andExpect(jsonPath("uuid").value(userDto.getUuid().toString()))
        ;

        assertThat(userRepository.count()).isEqualTo(countUsersBeforeCall + 1);

    }
}