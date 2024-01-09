package com.example.apexwh;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class SpanText {

    public class Span{

        public Span(Object what, int start, int end, int flags) {
            this.what = what;
            this.start = start;
            this.end = end;
            this.flags = flags;
        }

        public java.lang.Object what;
        public int start, end, flags;

    }

    public String text;
    public ArrayList<Span> spans;

    public SpanText() {
        this.text = "";
        this.spans = new ArrayList<>();
    }

    public void Append(String s){

        this.text = this.text + s;

    }

    public void AppendBold(String s){

        int curLength = this.text.length();
        this.text = this.text + s;

        this.spans.add(new Span(new StyleSpan(Typeface.BOLD), curLength, this.text.length(), 0));

    }

    public void AppendColor(String s, int color){

        int curLength = this.text.length();
        this.text = this.text + s;

        this.spans.add(new Span(new ForegroundColorSpan(color), curLength, this.text.length(), 0));

    }

    public SpannableString GetSpannableString(){

        SpannableString spanString = new SpannableString(this.text);

        this.spans.forEach(span -> {
            spanString.setSpan(span.what, span.start, span.end, span.flags);
        });

        return spanString;
    }

    public static SpannableString GetFilteredString(String text, String filter){

        SpanText spanText = new SpanText();

        Pattern pattern = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);

        String[] spl = pattern.split(text);

        if (text.toUpperCase().equals(filter.toUpperCase())){
            spanText.AppendColor(text, Color.RED);
        } else {

            for (int i = 0; i < spl.length - 1; i++) {

                spanText.Append(spl[i]);
                spanText.AppendColor(filter, Color.RED);
            }
            spanText.Append(spl[spl.length - 1]);
        }
        return spanText.GetSpannableString();


    }

}
