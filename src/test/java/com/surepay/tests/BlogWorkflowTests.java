package com.surepay.tests;

import java.util.List;
import org.testng.annotations.Test;
import com.surepay.framework.models.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlogWorkflowTests extends BaseTest {

    @Test(groups = {"regression"}, priority = 1)
    public void searchForUsers() {
        List<User> users = userService.getAllUsers();        
    }
}