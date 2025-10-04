package com.melikash98.customeditinputs;

import static androidx.core.content.res.TypedArrayUtils.getText;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public class CustomInput extends ConstraintLayout {
    //  Main properties
    private AppCompatEditText editText;
    private TextView hint, alert, helper;
    private ImageView start, end;

    //  Hint properties
    private int hintColor;
    private int hintFocusColor;
    private float hintNormalSize;
    private float hintFocusSize;

    //  Edit Text properties
    private int editColor;
    private float editSize;

    // Background
    private Drawable normalBg, focusBg;
    private int hintFocusBgColor;

    // State
    private boolean isFocused = false;
    // => Direction true = RTL, false = LTR
    private boolean isDirection = false;

    // Drawable Icon
    private Drawable hintIcon, editIcon;


    public CustomInput(@NonNull Context context) {
        super(context);
        Input(context, null);
    }

    public CustomInput(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Input(context, attrs);
    }

    public CustomInput(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Input(context, attrs);
    }

    /**
     * Initializes the view, inflates layout, reads attributes, sets listeners.
     */
    private void Input(Context context, AttributeSet attrs) {
        // Inflate layout
        LayoutInflater.from(context).inflate(R.layout.custom_input_layout, this, true);

        // Get all views
        editText = findViewById(R.id.edit_text_id);
        hint = findViewById(R.id.hint_id);
        alert = findViewById(R.id.alert_id);
        helper = findViewById(R.id.helper_id);

        // Default RTL/LTR based on layout direction
        isDirection = getLayoutDirection() == LAYOUT_DIRECTION_RTL;

        // Get attributes
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomEditTxtInputs);


            // Edit Style
            editText.setText(array.getString(R.styleable.CustomEditTxtInputs_editText));
            editColor = array.getColor(R.styleable.CustomEditTxtInputs_editTextColor, getResources().getColor(R.color.black));
            editSize = array.getDimension(R.styleable.CustomEditTxtInputs_editSize, 48f);
            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, editSize);
            editIcon = array.getDrawable(R.styleable.CustomEditTxtInputs_editDrawable);


            // Hint Style
            hint.setText(array.getString(R.styleable.CustomEditTxtInputs_hintText));
            hintColor = array.getColor(R.styleable.CustomEditTxtInputs_hintTextColor, getResources().getColor(R.color.dark_gray));
            hintFocusColor = array.getColor(R.styleable.CustomEditTxtInputs_hintFocusColor, getResources().getColor(R.color.black));
            hintNormalSize = array.getDimension(R.styleable.CustomEditTxtInputs_hintSize, editSize - 3);
            hintFocusSize = array.getDimension(R.styleable.CustomEditTxtInputs_hintFocusSize, hintNormalSize);
            hint.setTextSize(TypedValue.COMPLEX_UNIT_PX, hintNormalSize);
            hintFocusBgColor = array.getColor(R.styleable.CustomEditTxtInputs_hintFocusBg, Color.TRANSPARENT);
            hintIcon = array.getDrawable(R.styleable.CustomEditTxtInputs_hintDrawable);
            boolean isRight = array.getBoolean(R.styleable.CustomEditTxtInputs_isRight, false);
            isDirection = isRight;

            if (hintIcon != null) {
                if (isRight) {
                    hint.setCompoundDrawablesWithIntrinsicBounds(null, null, hintIcon, null);
                } else {
                    hint.setCompoundDrawablesWithIntrinsicBounds(hintIcon, null, null, null);
                }
            }
            if (editIcon != null) {
                if (isRight) {
                    end.setImageDrawable(editIcon);
                    end.setVisibility(VISIBLE);
                    start.setVisibility(GONE);
                } else {
                    start.setImageDrawable(editIcon);
                    start.setVisibility(VISIBLE);
                    end.setVisibility(GONE);
                }
            }

            // => Background Edit Text Style
            int bgResId = array.getResourceId(R.styleable.CustomEditTxtInputs_inputBox, -1);
            if (bgResId != -1) {
                normalBg = getResources().getDrawable(bgResId);
                editText.setBackground(normalBg);
            } else {
                normalBg = getResources().getDrawable(R.drawable.input_normal);
                editText.setBackground(normalBg);
            }
            int bgResId2 = array.getResourceId(R.styleable.CustomEditTxtInputs_inputFocusBox, -1);
            if (bgResId2 != -1) {
                focusBg = getResources().getDrawable(bgResId2);
            } else {
                focusBg = getResources().getDrawable(R.drawable.input_focus);
            }

            //  Locations


            array.recycle();

        }

        // Set properties
        editText.setBackground(normalBg);
        hint.setTextColor(hintColor);
        hint.setTextSize(TypedValue.COMPLEX_UNIT_PX, hintNormalSize);
        // Set listeners
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isFocused = hasFocus;
                updateHintState();
                updateBackground();
                updateAllPosition();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateHintState();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                updateHintState();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        updateAllPosition();
    }

    /**
     * Update hint background color only (keep shape same)
     */
    private void updateHintBackground(boolean focused) {
        if (hint.getBackground() != null) {
            Drawable bg = hint.getBackground().mutate();
            if (bg instanceof android.graphics.drawable.GradientDrawable) {
                if (focused) {
                    ((android.graphics.drawable.GradientDrawable) bg).setColor(hintFocusBgColor);
                } else {
                    ((android.graphics.drawable.GradientDrawable) bg).setColor(Color.TRANSPARENT);
                }
            }
        }
    }

    /**
     * Update hint animation based on focus and text
     */
    private void updateHintState() {
        boolean hasText = !TextUtils.isEmpty(editText.getText());
        boolean shouldFloat = isFocused || hasText;
        animateHint(hintNormalSize, hintFocusSize, shouldFloat);
    }


    /**
     * Animates hint position, size, color, and background
     */
    private void animateHint(float normalSize, float focusSize, boolean hasText) {
        float newSize = (isFocused || hasText) ? focusSize : normalSize;

        // --- Animate size ---
        ValueAnimator animator = ValueAnimator.ofFloat(hint.getTextSize(), newSize);
        animator.setDuration(200);
        animator.addUpdateListener(animation -> {
            float val = (float) animation.getAnimatedValue();
            hint.setTextSize(TypedValue.COMPLEX_UNIT_PX, val);
        });
        animator.start();

        // --- Animate position ---
        float translationY;
        float translationX;

        if (isFocused || hasText) {

            translationY = -75f;


            if (isDirection) {
                translationX = 35f;
            } else { // LTR
                translationX = -35f;
            }
        } else {
            translationY = 0f;
            translationX = 0f;
        }

        hint.animate().translationX(translationX).translationY(translationY).setDuration(200).start();

        //  Color of hint text
        hint.setTextColor(isFocused || hasText ? hintFocusColor : hintColor);
        if (hintIcon != null) {
            int colorToApply = isFocused || hasText ? hintFocusColor : hintColor;
            hintIcon.setColorFilter(new android.graphics.PorterDuffColorFilter(colorToApply, android.graphics.PorterDuff.Mode.SRC_IN));
        }

        //  Background color of hint box
        updateHintBackground(isFocused || hasText);

        // EditText background
        editText.setBackground(isFocused ? focusBg : normalBg);
    }


    /**
     * Updates EditText background
     */
    private void updateBackground() {
        if (isFocused) {
            editText.setBackground(focusBg != null ? focusBg : normalBg);
        } else {
            editText.setBackground(normalBg);
        }
    }


    /**
     * Updates gravity/position for text, hint, helper, alert and icons
     */
    private void updateAllPosition() {
        int gravityStart = isDirection ? Gravity.END : Gravity.START;
        setLayoutDirection(isDirection ? LAYOUT_DIRECTION_RTL : LAYOUT_DIRECTION_LTR);

        hint.setGravity(gravityStart | Gravity.CENTER_VERTICAL);
        helper.setGravity(gravityStart);
        alert.setGravity(gravityStart);
        editText.setGravity(gravityStart | Gravity.CENTER_VERTICAL);

        //updateIcon(start, true);
        //updateIcon(end, false);
    }


    /**
     * Update icon position based on RTL/LTR
     */
    /*
    * private void updateIcon(ImageView icon, boolean isStartIcon) {
        if (icon.getVisibility() != VISIBLE) return;
        LayoutParams params = (LayoutParams) icon.getLayoutParams();
        if (isStartIcon) {
            params.gravity = isDirection ? Gravity.END | Gravity.CENTER_VERTICAL : Gravity.START | Gravity.CENTER_VERTICAL;
        } else {
            params.gravity = isDirection ? Gravity.START | Gravity.CENTER_VERTICAL : Gravity.END | Gravity.CENTER_VERTICAL;
        }
        icon.setLayoutParams(params);
    }*/


    /**
     * Manually set input direction RTL/LTR
     */
    public void setInputDirection(boolean rtl) {
        isDirection = rtl;
        updateAllPosition();
    }

    //  Getters and Setters
    // => Helper Text
    public void setHelperText(String text) {
        helper.setText(text);
        helper.setVisibility(VISIBLE);
        updateAllPosition();
    }

    public void hideHelper() {
        helper.setVisibility(GONE);
    }

    // => Alert Text
    public void setAlertText(String text) {
        alert.setText(text);
        alert.setVisibility(VISIBLE);
        updateAllPosition();
    }

    public void hideAlert() {
        alert.setVisibility(GONE);
    }


    // Expose EditText
    public String getInputText() {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    public int getInputInt() {
        try {
            return Integer.parseInt(getInputText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public double getInputDouble() {
        try {
            return Double.parseDouble(getInputText());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public boolean getInputBoolean() {
        String val = getInputText().toLowerCase();
        return val.equals("true") || val.equals("1") || val.equals("yes");
    }

    public boolean isEmail() {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(getInputText()).matches();
    }

    public boolean isPhone() {
        return android.util.Patterns.PHONE.matcher(getInputText()).matches();
    }


}
