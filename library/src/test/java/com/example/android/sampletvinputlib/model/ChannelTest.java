/*
 * Copyright 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sampletvinputlib.model;

import android.content.ContentValues;
import android.content.Intent;
import android.database.MatrixCursor;
import android.media.tv.TvContract;

import com.example.android.sampletvinput.model.Channel;
import com.example.android.sampletvinput.model.InternalProviderData;
import com.example.android.sampletvinputlib.BuildConfig;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Tests that channels can be created using the Builder pattern and correctly obtain
 * values from them
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23, manifest = "src/main/AndroidManifest.xml")
public class ChannelTest extends TestCase {
    private static final String KEY_SPLASHSCREEN = "splashscreen";
    private static final String KEY_PREMIUM_CHANNEL = "premium";
    private static final String SPLASHSCREEN_URL = "http://example.com/splashscreen.jpg";

    @Test
    public void testEmptyChannel() {
        // Tests creating an empty channel and handling the error because it's missing required
        // attributes.
        try {
            Channel emptyChannel = new Channel.Builder()
                    .build();
            ContentValues contentValues = emptyChannel.toContentValues();
            compareChannel(emptyChannel, Channel.fromCursor(getChannelCursor(contentValues)));
            fail("A channel should not be allowed to exist with an undefined original network id.");
        } catch (IllegalArgumentException ignored) {
            // Exception correctly handled
        }
    }

    @Test
    public void testSampleChannel() {
        // Tests cloning and database I/O of a channel with some defined and some undefined
        // values.
        Channel sampleChannel = new Channel.Builder()
                .setDisplayName("Google")
                .setDisplayNumber("3")
                .setDescription("This is a sample channel")
                .setOriginalNetworkId(1)
                .setAppLinkIntentUri(new Intent().toUri(Intent.URI_INTENT_SCHEME))
                .setOriginalNetworkId(0)
                .build();
        ContentValues contentValues = sampleChannel.toContentValues();
        compareChannel(sampleChannel, Channel.fromCursor(getChannelCursor(contentValues)));

        Channel clonedSampleChannel = new Channel.Builder(sampleChannel).build();
        compareChannel(sampleChannel, clonedSampleChannel);
    }

    @Test
    public void testFullyPopulatedChannel() throws InternalProviderData.ParseException {
        // Tests cloning and database I/O of a channel with every value being defined.
        InternalProviderData internalProviderData = new InternalProviderData();
        internalProviderData.setRepeatable(true);
        internalProviderData.put(KEY_SPLASHSCREEN, SPLASHSCREEN_URL);
        internalProviderData.put(KEY_PREMIUM_CHANNEL, false);
        Channel fullyPopulatedChannel = new Channel.Builder()
                .setAppLinkColor(RuntimeEnvironment.application.getResources()
                        .getColor(android.R.color.holo_orange_light))
                .setAppLinkIconUri("http://example.com/icon.png")
                .setAppLinkIntent(new Intent())
                .setAppLinkPosterArtUri("http://example.com/poster.png")
                .setAppLinkText("Open an intent")
                .setDescription("Channel description")
                .setDisplayName("Display Name")
                .setDisplayNumber("100")
                .setInternalProviderData(internalProviderData)
                .setInputId("TestInputService")
                .setNetworkAffiliation("Network Affiliation")
                .setOriginalNetworkId(2)
                .setPackageName("com.example.android.sampletvinput")
                .setSearchable(false)
                .setServiceId(3)
                .setTransportStreamId(4)
                .setType(TvContract.Channels.TYPE_1SEG)
                .setServiceType(TvContract.Channels.SERVICE_TYPE_AUDIO_VIDEO)
                .setVideoFormat(TvContract.Channels.VIDEO_FORMAT_240P)
                .build();
        ContentValues contentValues = fullyPopulatedChannel.toContentValues();
        compareChannel(fullyPopulatedChannel, Channel.fromCursor(getChannelCursor(contentValues)));

        Channel clonedFullyPopulatedChannel = new Channel.Builder(fullyPopulatedChannel).build();
        compareChannel(fullyPopulatedChannel, clonedFullyPopulatedChannel);
    }

    private static void compareChannel(Channel channelA, Channel channelB) {
        assertEquals(channelA.getAppLinkColor(), channelB.getAppLinkColor());
        assertEquals(channelA.getAppLinkIconUri(), channelB.getAppLinkIconUri());
        assertEquals(channelA.getAppLinkIntentUri(), channelB.getAppLinkIntentUri());
        assertEquals(channelA.getAppLinkPosterArtUri(), channelB.getAppLinkPosterArtUri());
        assertEquals(channelA.getAppLinkText(), channelB.getAppLinkText());
        assertEquals(channelA.isSearchable(), channelB.isSearchable());
        assertEquals(channelA.getDescription(), channelB.getDescription());
        assertEquals(channelA.getDisplayName(), channelB.getDisplayName());
        assertEquals(channelA.getDisplayNumber(), channelB.getDisplayNumber());
        assertEquals(channelA.getId(), channelB.getId());
        assertEquals(channelA.getInputId(), channelB.getInputId());
        assertEquals(channelA.getInternalProviderData(), channelB.getInternalProviderData());
        assertEquals(channelA.getNetworkAffiliation(), channelB.getNetworkAffiliation());
        assertEquals(channelA.getOriginalNetworkId(), channelB.getOriginalNetworkId());
        assertEquals(channelA.getPackageName(), channelB.getPackageName());
        assertEquals(channelA.getServiceId(), channelB.getServiceId());
        assertEquals(channelA.getServiceType(), channelB.getServiceType());
        assertEquals(channelA.getTransportStreamId(), channelB.getTransportStreamId());
        assertEquals(channelA.getType(), channelB.getType());
        assertEquals(channelA.getVideoFormat(), channelB.getVideoFormat());
        assertEquals(channelA.toContentValues(), channelB.toContentValues());
        assertEquals(channelA.toString(), channelB.toString());
    }

    private static MatrixCursor getChannelCursor(ContentValues contentValues) {
        String[] rows = new String[] {
                TvContract.Channels._ID,
                TvContract.Channels.COLUMN_APP_LINK_COLOR,
                TvContract.Channels.COLUMN_APP_LINK_ICON_URI,
                TvContract.Channels.COLUMN_APP_LINK_INTENT_URI,
                TvContract.Channels.COLUMN_APP_LINK_POSTER_ART_URI,
                TvContract.Channels.COLUMN_APP_LINK_TEXT,
                TvContract.Channels.COLUMN_DESCRIPTION,
                TvContract.Channels.COLUMN_DISPLAY_NAME,
                TvContract.Channels.COLUMN_DISPLAY_NUMBER,
                TvContract.Channels.COLUMN_INPUT_ID,
                TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA,
                TvContract.Channels.COLUMN_NETWORK_AFFILIATION,
                TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID,
                TvContract.Channels.COLUMN_PACKAGE_NAME,
                TvContract.Channels.COLUMN_SEARCHABLE,
                TvContract.Channels.COLUMN_SERVICE_ID,
                TvContract.Channels.COLUMN_SERVICE_TYPE,
                TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID,
                TvContract.Channels.COLUMN_TYPE,
                TvContract.Channels.COLUMN_VERSION_NUMBER,
                TvContract.Channels.COLUMN_VIDEO_FORMAT
        };
        MatrixCursor cursor = new MatrixCursor(rows);
        MatrixCursor.RowBuilder builder = cursor.newRow();
        for(String row: rows) {
            builder.add(row, contentValues.get(row));
        }
        cursor.moveToFirst();
        return cursor;
    }
}
