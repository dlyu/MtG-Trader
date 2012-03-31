package com.mtg.trade.guide;

import java.util.EventListener;

public interface CardAttributeChangedListener extends EventListener {
	public void onQuantityChange(CardAttributeChangedEvent q);
	public void onRemove();
}