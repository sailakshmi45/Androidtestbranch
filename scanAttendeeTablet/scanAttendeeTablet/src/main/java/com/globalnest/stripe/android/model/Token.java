package com.globalnest.stripe.android.model;

import java.util.Date;

// This is different from Token in com.stripe.model because it does not
public class Token extends com.stripe.model.StripeObject {
  String id;
  Date created;
  boolean livemode;
  boolean used;
  Card card;

  public Date getCreated() {
    return created;
  }

  public String getId() {
    return id;
  }

  public boolean getLivemode() {
    return livemode;
  }

  public boolean getUsed() {
    return used;
  }

  public Card getCard() {
    return card;
  }

 /*
 This method should not be invoked in your code.  This is used by Stripe to
 create tokens using a Stripe API response
 */
  public Token(String id, boolean livemode, Date created, Boolean used, Card card) {
        this.id = id;
        this.livemode = livemode;
        this.card = card;
        this.created = created;
        this.used = used;
    }
}