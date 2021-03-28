package com.example.gcpapp.authentication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.EditText;

public class OTPReceiver extends BroadcastReceiver {

    private static EditText editText1;
    private static EditText editText2;
    private static EditText editText3;
    private static EditText editText4;
    private static EditText editText5;
    private static EditText editText6;


    public void setEditText(EditText editText1,EditText editText2,EditText editText3,EditText editText4,EditText editText5,EditText editText6)
    {
        OTPReceiver.editText1 = editText1;
        OTPReceiver.editText2 = editText2;
        OTPReceiver.editText3 = editText3;
        OTPReceiver.editText4 = editText4;
        OTPReceiver.editText5 = editText5;
        OTPReceiver.editText6 = editText6;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

        for (SmsMessage sms : messages)
        {
            String message = sms.getMessageBody();
            String otp = message.substring(0,6);
            String otp1 = otp.substring(0,1);
            String otp2 = otp.substring(1,2);
            String otp3 = otp.substring(2,3);
            String otp4 = otp.substring(3,4);
            String otp5 = otp.substring(4,5);
            String otp6 = otp.substring(5,6);
            editText1.setText(otp1);
            editText2.setText(otp2);
            editText3.setText(otp3);
            editText4.setText(otp4);
            editText5.setText(otp5);
            editText6.setText(otp6);
        }

    }
}
