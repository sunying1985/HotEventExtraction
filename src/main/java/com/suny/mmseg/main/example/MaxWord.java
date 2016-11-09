package com.suny.mmseg.main.example;

import java.io.IOException;

import com.suny.mmseg.main.MaxWordSeg;
import com.suny.mmseg.main.Seg;

public class MaxWord extends Complex {

	protected Seg getSeg() {

		return new MaxWordSeg(dic);
	}

	public static void main(String[] args) throws IOException {
		new MaxWord().run(args);
	}
}
