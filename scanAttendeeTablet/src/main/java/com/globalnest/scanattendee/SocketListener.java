package com.globalnest.scanattendee;

import com.globalnest.objects.WebsocketResponseObj;

public interface SocketListener {
    public void onTstatusrecieved(WebsocketResponseObj attendeeid);
    public void onPrintstatusrecieved(WebsocketResponseObj attendeeid);

}
