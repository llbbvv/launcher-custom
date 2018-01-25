package org.literacyapp.launcher_custom;

import org.junit.Test;

import java.io.File;

import ai.elimu.launcher_custom.AppCollectionGenerator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class AppCollectionGeneratorTest {

    @Test
    public void testLoadAppCollectionFromJsonFile() {
        File jsonFile = new File("app/src/test/assets/app-collection.json");
        System.out.println("jsonFile.getAbsolutePath(): " + jsonFile.getAbsolutePath());
        System.out.println("jsonFile.exists(): " + jsonFile.exists());
//        AppCollection appCollection = AppCollectionGenerator.loadAppCollectionFromJsonFile(jsonFile);
//        assertThat(appCollection, is(notNullValue()));
//        assertThat(appCollection.getAppCategories().size(), is(10));

    }
}