/*
 *  Copyright (c) 2011 by Twilio, Inc., all rights reserved.
 *
 *  Use of this software is subject to the terms and conditions of 
 *  the Twilio Terms of Service located at http://www.twilio.com/legal/tos
 */

package com.twilio.example.callingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.twilio.client.Connection;
import com.twilio.client.Device;

public class CallingActivity extends Activity implements View.OnClickListener
{
    private static final String TAG = "CallingActivity";
    private Phone phone;
    private EditText numberField;

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.main);

        phone = new Phone(getApplicationContext());

        ImageButton dialButton = (ImageButton)findViewById(R.id.dialButton);
        dialButton.setOnClickListener(this);

        ImageButton hangupButton = (ImageButton)findViewById(R.id.hangupButton);
        hangupButton.setOnClickListener(this);

        numberField = (EditText)findViewById(R.id.numberField);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.dialButton) {
            phone.connect(numberField.getText().toString());
        } else if (view.getId() == R.id.hangupButton) {
            phone.disconnect();
        }
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Intent intent = getIntent();
        Device device = intent.getParcelableExtra(Device.EXTRA_DEVICE);
        Connection connection = intent.getParcelableExtra(Device.EXTRA_CONNECTION);
        if (device != null && connection != null) {
            intent.removeExtra(Device.EXTRA_DEVICE);
            intent.removeExtra(Device.EXTRA_CONNECTION);
            phone.handleIncomingConnection(device, connection);
        }
    }

}