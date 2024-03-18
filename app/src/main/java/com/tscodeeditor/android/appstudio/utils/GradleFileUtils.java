/*
 * This file is part of Android AppStudio [https://github.com/TS-Code-Editor/AndroidAppStudio].
 *
 * License Agreement
 * This software is licensed under the terms and conditions outlined below. By accessing, copying, modifying, or using this software in any way, you agree to abide by these terms.
 *
 * 1. **  Copy and Modification Restrictions  **
 *    - You are not permitted to copy or modify the source code of this software without the permission of the owner, which may be granted publicly on GitHub Discussions or on Discord.
 *    - If permission is granted by the owner, you may copy the software under the terms specified in this license agreement.
 *    - You are not allowed to permit others to copy the source code that you were allowed to copy by the owner.
 *    - Modified or copied code must not be further copied.
 * 2. **  Contributor Attribution  **
 *    - You must attribute the contributors by creating a visible list within the application, showing who originally wrote the source code.
 *    - If you copy or modify this software under owner permission, you must provide links to the profiles of all contributors who contributed to this software.
 * 3. **  Modification Documentation  **
 *    - All modifications made to the software must be documented and listed.
 *    - the owner may incorporate the modifications made by you to enhance this software.
 * 4. **  Consistent Licensing  **
 *    - All copied or modified files must contain the same license text at the top of the files.
 * 5. **  Permission Reversal  **
 *    - If you are granted permission by the owner to copy this software, it can be revoked by the owner at any time. You will be notified at least one week in advance of any such reversal.
 *    - In case of Permission Reversal, if you fail to acknowledge the notification sent by us, it will not be our responsibility.
 * 6. **  License Updates  **
 *    - The license may be updated at any time. Users are required to accept and comply with any changes to the license.
 *    - In such circumstances, you will be given 7 days to ensure that your software complies with the updated license.
 *    - We will not notify you about license changes; you need to monitor the GitHub repository yourself (You can enable notifications or watch the repository to stay informed about such changes).
 * By using this software, you acknowledge and agree to the terms and conditions outlined in this license agreement. If you do not agree with these terms, you are not permitted to use, copy, modify, or distribute this software.
 *
 * Copyright © 2024 Dev Kumar
 */

package com.tscodeeditor.android.appstudio.utils;

import com.tscodeeditor.android.appstudio.R;
import com.tscodeeditor.android.appstudio.block.model.Event;
import com.tscodeeditor.android.appstudio.block.model.FileModel;
import com.tscodeeditor.android.appstudio.block.utils.RawCodeReplacer;
import com.tscodeeditor.android.appstudio.models.EventHolder;
import com.tscodeeditor.android.appstudio.utils.serialization.SerializerUtil;
import java.io.File;
import java.util.ArrayList;

public class GradleFileUtils {
  private static FileModel getAppModuleGradleFileModule() {
    FileModel appModuleGradleFile = new FileModel();
    appModuleGradleFile.setFileName("build");
    appModuleGradleFile.setFileExtension("gradle");
    appModuleGradleFile.setFolder(false);

    StringBuilder appModuleGradleFileRawCode = new StringBuilder();
    appModuleGradleFileRawCode.append("plugins {\n\tid 'com.android.application'\n}\n");
    appModuleGradleFileRawCode.append(RawCodeReplacer.getReplacer("androidBlock"));
    appModuleGradleFileRawCode.append(RawCodeReplacer.getReplacer("dependenciesBlock"));

    appModuleGradleFile.setRawCode(appModuleGradleFileRawCode.toString());

    ArrayList<Event> builtinEvents = new ArrayList<Event>();

    Event androidBlockEvent = new Event();
    androidBlockEvent.setTitle("App Configration");
    androidBlockEvent.setName("androidBlock");
    androidBlockEvent.setDescription("Contains basic defination of your app");
    androidBlockEvent.setEventReplacer("blockCode");
    androidBlockEvent.setRawCode("android {\n" + RawCodeReplacer.getReplacer("blockCode") + "\n}");

    Event dependenciesBlockEvent = new Event();
    dependenciesBlockEvent.setTitle("App Libraries");
    dependenciesBlockEvent.setName("dependenciesBlock");
    dependenciesBlockEvent.setDescription("Contains library used by your app");
    dependenciesBlockEvent.setEventReplacer("blockCode");
    dependenciesBlockEvent.setRawCode(
        "dependencies {\n" + RawCodeReplacer.getReplacer("blockCode") + "\n}");

    builtinEvents.add(androidBlockEvent);
    builtinEvents.add(dependenciesBlockEvent);

    appModuleGradleFile.setDefaultBuiltInEvents(builtinEvents);

    return appModuleGradleFile;
  }

  private static EventHolder getGradleEventHolder() {
    EventHolder eventHolder = new EventHolder();
    eventHolder.setBuiltInEvents(true);
    eventHolder.setHolderName("Config");
    eventHolder.setIcon(R.drawable.ic_gradle);
    return eventHolder;
  }

  public static void createGradleFilesIfDoNotExists(File projectRootDirectory) {
    if (!EnvironmentUtils.getAppGradleFile(projectRootDirectory).exists()) {
      if (!EnvironmentUtils.getAppGradleFile(projectRootDirectory).getParentFile().exists()) {
        EnvironmentUtils.getAppGradleFile(projectRootDirectory).getParentFile().mkdirs();
      }

      /*
       * Generates app folder to store app/build.gradle.
       */
      SerializerUtil.serialize(
          FileModelUtils.getFolderModel("app"),
          new File(
              EnvironmentUtils.getAppGradleFile(projectRootDirectory)
                  .getParentFile()
                  .getParentFile(),
              EnvironmentUtils.FILE_MODEL),
          new SerializerUtil.SerializerCompletionListener() {

            @Override
            public void onSerializeComplete() {}

            @Override
            public void onFailedToSerialize(Exception exception) {}
          });

      /*
       * Generate app module build.gradle file.
       */
      if (!EnvironmentUtils.getAppGradleFile(projectRootDirectory).exists()) {
        EnvironmentUtils.getAppGradleFile(projectRootDirectory).mkdirs();
      }

      SerializerUtil.serialize(
          getAppModuleGradleFileModule(),
          new File(
              EnvironmentUtils.getAppGradleFile(projectRootDirectory), EnvironmentUtils.FILE_MODEL),
          new SerializerUtil.SerializerCompletionListener() {

            @Override
            public void onSerializeComplete() {}

            @Override
            public void onFailedToSerialize(Exception exception) {}
          });
    }

    /*
     * Install Gradle Config EventHolder for app module gradle file
     */

    if (!new File(
            new File(
                EnvironmentUtils.getAppGradleFile(projectRootDirectory),
                EnvironmentUtils.EVENTS_DIR),
            EnvironmentUtils.APP_GRADLE_CONFIG_EVENT_HOLDER)
        .exists()) {
      new File(
              new File(
                  EnvironmentUtils.getAppGradleFile(projectRootDirectory),
                  EnvironmentUtils.EVENTS_DIR),
              EnvironmentUtils.APP_GRADLE_CONFIG_EVENT_HOLDER)
          .mkdirs();
    }

    if (!new File(
            new File(
                new File(
                    EnvironmentUtils.getAppGradleFile(projectRootDirectory),
                    EnvironmentUtils.EVENTS_DIR),
                EnvironmentUtils.APP_GRADLE_CONFIG_EVENT_HOLDER),
            EnvironmentUtils.EVENTS_HOLDER)
        .exists()) {
      SerializerUtil.serialize(
          getGradleEventHolder(),
          new File(
              new File(
                  new File(
                      EnvironmentUtils.getAppGradleFile(projectRootDirectory),
                      EnvironmentUtils.EVENTS_DIR),
                  EnvironmentUtils.APP_GRADLE_CONFIG_EVENT_HOLDER),
              EnvironmentUtils.EVENTS_HOLDER),
          new SerializerUtil.SerializerCompletionListener() {

            @Override
            public void onSerializeComplete() {}

            @Override
            public void onFailedToSerialize(Exception exception) {}
          });
    }
  }
}
