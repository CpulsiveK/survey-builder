//package com.amalitech.surveysphere.controllers;
//
//import com.amalitech.surveysphere.dto.requestDto.UserDto;
//import com.amalitech.surveysphere.dto.responseDto.UserResponseDto;
//import com.amalitech.surveysphere.models.User;
//import com.amalitech.surveysphere.repositories.UserRepository;
//import com.amalitech.surveysphere.services.responseManagementService.ResponseManagementService;
//import com.amalitech.surveysphere.services.socialUserService.SocialUserService;
//import com.amalitech.surveysphere.services.surveyService.SurveyService;
//import com.amalitech.surveysphere.services.userService.UserService;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@RunWith(MockitoJUnitRunner.class)
//public class PublicControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private  SocialUserService socialUserService;
//    @Mock
//    private  UserService userService;
//    @Mock
//    private  SurveyService surveyService;
//
//    @Mock
//    private ResponseManagementService responseManagementService;
//
//    private PublicController publicController;
//
//    User user1 = new User("asdf", "emma", "username", "user@email.com", "1234", "USER", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),null,true,true);
//    User user2 = new User("asdfasd", "trigger", "username1", "user1@email" +
//            ".com", "1234", "USER", new ArrayList<>(), new ArrayList<>(),
//            new ArrayList<>(),null,true,true);
//
//    @Before
//    public void setUp() {
//        publicController= new PublicController(socialUserService,userService,
//                surveyService,userRepository,responseManagementService);
//    this.mockMvc = MockMvcBuilders.standaloneSetup(publicController).build();
//    }
//
//    @Test
//    public void getAllUsersSuccess() throws Exception {
//        List<User> users = Arrays.asList(user1, user2);
//
//        Mockito.when(userRepository.findAll()).thenReturn(users);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/survey-sphere/public/get-users")  // Correct endpoint path
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)));
//
//        verify(userRepository, times(1)).findAll();
//    }
//
//    @Test
//    public void testRegisterSuccess() throws Exception {
//
//
//        UserResponseDto registeredUser = new UserResponseDto("1","accessToken","linda pomaa","pomaa","linda@email.com",new ArrayList<>(),"ROLE",true,null);
//
//        Mockito.when(userService.registerUser(any(UserDto.class))).thenReturn(registeredUser);
//
//       MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
//                        .post("/survey-sphere/public/signup")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"username\":\"pomaa\",\"email\":\"linda@email.com\",\"password1\":\"1234\",\"name\":\"linda pomaa\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.username").value(registeredUser.getUsername()))
//                .andExpect(jsonPath("$.email").value(registeredUser.getEmail()))
//                .andExpect(jsonPath("$.password").doesNotExist())
//                .andExpect(jsonPath("$.name").value(registeredUser.getName()))
//                .andExpect(jsonPath("$.username").exists())
//                .andExpect(jsonPath("$.email").exists())
//                .andExpect(jsonPath("$.password").doesNotExist())
//                .andExpect(jsonPath("$.name").exists())
//                        .andReturn();
//
//        String content = mvcResult.getResponse().getContentAsString();
//
//        // Perform expectations on specific fields using the extracted content
//        assertThat(content).contains("\"username\":\"pomaa\"");
//        assertThat(content).contains("\"email\":\"linda@email.com\"");
//        assertThat(content).doesNotContain("\"password\"");
//        assertThat(content).contains("\"name\":\"linda pomaa\"");
//        verify(userService, times(1)).registerUser(any(UserDto.class));
//    }
//}