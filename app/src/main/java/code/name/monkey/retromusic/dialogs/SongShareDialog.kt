/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package code.name.monkey.retromusic.dialogs

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.activities.ShareInstagramStory
import code.name.monkey.retromusic.model.CommonData
import code.name.monkey.retromusic.model.Song
import code.name.monkey.retromusic.util.MusicUtil
import code.name.monkey.retromusic.util.PreferenceUtil
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems

class SongShareDialog : DialogFragment() {

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (ignore: IllegalStateException) {
            ignore.printStackTrace()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val song: CommonData? = requireArguments().getParcelable("song")
        val currentlyListening: String =
            getString(
                R.string.currently_listening_to_x_by_x,
                song?.getSongTitle(),
                song?.getSongSinger()
            )

        return MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT))
            .title(R.string.what_do_you_want_to_share)
            .show {
                cornerRadius(PreferenceUtil.getInstance(requireContext()).dialogCorner)
                listItems(
                    items = listOf(
                        getString(code.name.monkey.retromusic.R.string.the_audio_file),
                        "\u201C" + currentlyListening + "\u201D",
                        getString(R.string.social_stories)
                    )
                ) { _, index, _ ->
                    when (index) {
                        0 -> {
                            startActivity(Intent.createChooser(song?.let {
                                MusicUtil.createShareSongFileIntent(
                                    it,
                                    context
                                )
                            }, null))
                        }
                        1 -> {
                            startActivity(
                                Intent.createChooser(
                                    Intent()
                                        .setAction(Intent.ACTION_SEND)
                                        .putExtra(Intent.EXTRA_TEXT, currentlyListening)
                                        .setType("text/plain"),
                                    null
                                )
                            )
                        }
                        2 -> {
                            if (song != null) {
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        ShareInstagramStory::class.java
                                    ).putExtra(
                                        ShareInstagramStory.EXTRA_SONG,
                                        song
                                    )
                                )
                            }
                        }
                    }
                }
            }
    }

    companion object {

        fun create(song: CommonData): SongShareDialog {
            val dialog = SongShareDialog()
            val args = Bundle()
            args.putParcelable("song", song)
            dialog.arguments = args
            return dialog
        }
    }
}
