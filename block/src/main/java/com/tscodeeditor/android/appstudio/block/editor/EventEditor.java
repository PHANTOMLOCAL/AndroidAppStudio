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

package com.tscodeeditor.android.appstudio.block.editor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.tscodeeditor.android.appstudio.block.adapter.BlocksHolderAdapter;
import com.tscodeeditor.android.appstudio.block.databinding.EventEditorLayoutBinding;
import com.tscodeeditor.android.appstudio.block.model.BlockHolderModel;
import com.tscodeeditor.android.appstudio.block.model.BlockModel;
import com.tscodeeditor.android.appstudio.block.model.Event;
import com.tscodeeditor.android.appstudio.block.utils.TargetUtils;
import com.tscodeeditor.android.appstudio.block.utils.UnitUtils;
import com.tscodeeditor.android.appstudio.block.view.BlockDragView;
import java.util.ArrayList;

public class EventEditor extends RelativeLayout {

  public EventEditorLayoutBinding binding;
  public BlockDragView blockFloatingView;

  public boolean isDragging = false;

  // Contants for showing the section easily
  public static final int LOADING_SECTION = 0;
  public static final int INFO_SECTION = 1;
  public static final int EDITOR_SECTION = 2;
  public static final int VALUE_EDITOR_SECTION = 3;

  public EventEditor(final Context context, final AttributeSet set) {
    super(context, set);

    binding = EventEditorLayoutBinding.inflate(LayoutInflater.from(context));
    blockFloatingView = new BlockDragView(context, null);
    binding.getRoot().setClipChildren(true);
    addView(binding.getRoot());
    setMatchParent(binding.getRoot());
    switchSection(EDITOR_SECTION);
    invalidate();
  }

  private void setMatchParent(View view) {
    RelativeLayout.LayoutParams layoutParams =
        new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
    view.setLayoutParams(layoutParams);
  }

  /*
   * Method for switching the section quickly.
   * All other section will be GONE except the section of which the section code is provided
   */
  public void switchSection(int section) {
    binding.loading.setVisibility(section == LOADING_SECTION ? View.VISIBLE : View.GONE);
    binding.info.setVisibility(section == INFO_SECTION ? View.VISIBLE : View.GONE);
    binding.editorSection.setVisibility(section == EDITOR_SECTION ? View.VISIBLE : View.GONE);
    binding.valueEditorSection.setVisibility(
        section == VALUE_EDITOR_SECTION ? View.VISIBLE : View.GONE);
  }

  public void showBlocksPallete(boolean show) {
    binding.blockArea.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  public void initEditor(Event event) {
    binding.canva.initEditor(event);
  }

  public void setHolder(ArrayList<BlockHolderModel> holderList) {
    binding.blocksHolderList.setAdapter(new BlocksHolderAdapter(holderList, this));
    binding.blocksHolderList.setLayoutManager(new LinearLayoutManager(getContext()));
  }

  public void startBlockDrag(BlockModel block, float x, float y) {
    binding.canva.setAllowScroll(false);
    binding.blockList.requestDisallowInterceptTouchEvent(true);
    isDragging = true;
    blockFloatingView.setBlock(block);
    addView(blockFloatingView);
    RelativeLayout.LayoutParams blockFloatingViewParam =
        new RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    blockFloatingView.setLayoutParams(blockFloatingViewParam);
    blockFloatingView.setX(x);
    blockFloatingView.setY(y);
    blockFloatingView.setAllowed(isBlockFloatingViewInsideCanva(x, y));
  }

  public void stopDrag() {
    isDragging = false;
    binding.canva.setAllowScroll(true);
    binding.blockList.requestDisallowInterceptTouchEvent(false);
    removeView(blockFloatingView);
  }

  public void moveFloatingBlockView(float x, float y) {
    blockFloatingView.setX(x);
    blockFloatingView.setY(y);
    blockFloatingView.setAllowed(isBlockFloatingViewInsideCanva(x, y));
  }

  public boolean isBlockFloatingViewInsideCanva(float x, float y) {
    int notAllowedIconWidth = 0;
    if (blockFloatingView.notAllowed != null) {
      if (blockFloatingView.notAllowed.getParent() != null) {
        notAllowedIconWidth = blockFloatingView.notAllowed.getWidth();
      }
    }

    return TargetUtils.isPointInsideRectangle(
        (int) x + notAllowedIconWidth,
        (int) y,
        0,
        0,
        binding.editorSection.getWidth(),
        binding.editorSection.getHeight());
  }
}
