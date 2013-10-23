package com.donomobile.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.donomobile.web.rskybox.CreateClientLogTask;

import android.text.InputFilter;
import android.text.Spanned;

public class CurrencyFilter implements InputFilter {

	Pattern mPattern = Pattern.compile("(0|[1-9]+[0-9]*)?(\\.[0-9]{0,2})?");

	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {

		try {
			String result = dest.subSequence(0, dstart) + source.toString()
					+ dest.subSequence(dend, dest.length());

			Matcher matcher = mPattern.matcher(result);

			if (!matcher.matches())
				return dest.subSequence(dstart, dend);

			return null;
		} catch (Exception e) {
			(new CreateClientLogTask("CurrencyFilter.filter", "Exception Caught", "error", e)).execute();
			return null;

		}
	}
}
