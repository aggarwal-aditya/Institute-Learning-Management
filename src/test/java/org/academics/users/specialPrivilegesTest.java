package org.academics.users;

import org.academics.dal.dbUtils;
import org.academics.utility.Utils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class specialPrivilegesTest {

    @Test
    public void testGetPreRequisites() {
        try (MockedStatic<Utils> utilsMock = mockStatic(Utils.class)) {
            ArrayList<String> preReqs = specialPrivileges.getPreRequisites();
            when(Utils.getInput(anyString())).thenReturn("CS101\n").thenReturn("B\n").thenReturn("Y\n").thenReturn("CS102\n").thenReturn("B\n").thenReturn("N\n").thenReturn("CS103\n").thenReturn("E\n").thenReturn("N\n").thenReturn("N\n");
            specialPrivileges.getPreRequisites();
        }
//        assertTrue(pre);
    }

    @Test
    public  void testviewDepartmentIDs() throws SQLException {
        MockedStatic<Utils>utilsMockedStatic= mockStatic(Utils.class);
        MockedStatic<dbUtils>dbUtilsMockedStatic= mockStatic(dbUtils.class);
        //do nothing when utils.printTable is called
        ResultSet resultSet= mock(ResultSet.class);
        dbUtilsMockedStatic.when(dbUtils::getDepartmentIDs).thenReturn(resultSet);
        specialPrivileges.viewDepartmentIDs();
        //verify getDepartmentIDs was called
    }

}