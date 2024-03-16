package com.example.rebootapp;
import android.content.Context;

import org.junit.Test;
import androidx.test.platform.app.InstrumentationRegistry;
import static org.junit.Assert.*;

import com.example.rebootapp.Adapters.GameListAdapter;
import com.example.rebootapp.Models.UserListModel;

public class CustomListTest {

    @Test
    public void itemCountTest() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        UserListModel testList = new UserListModel("user123", null, null, null, "MyList", "list123");
        GameListAdapter test = new GameListAdapter(context, testList);
        int result = test.getItemCount();
        assertEquals(0, result);
    }
}
