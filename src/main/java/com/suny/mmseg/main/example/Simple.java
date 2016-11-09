package com.suny.mmseg.main.example;

import java.io.IOException;

import com.suny.mmseg.main.Seg;
import com.suny.mmseg.main.SimpleSeg;

/**
 * 
 * @author suny 20140721
 */
public class Simple extends Complex {
	
	protected Seg getSeg() {

		return new SimpleSeg(dic);
	}

	public static void main(String[] args) throws IOException {
		new Simple().run(args);
	}

}
