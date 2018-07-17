package se.mah.af2015.dinekonomi;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class AmountTextView extends TextView {
    public AmountTextView(Context context) {
        super(context);
    }

    public AmountTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AmountTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AmountTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        StringBuilder sb = new StringBuilder();
        sb.append(text + " kr");
        super.setText(sb, type);
    }
}
