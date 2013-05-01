package org.cruft4j.sample;

import java.util.Date;

public class SampleFile {

	/**
	 * This is a really long crappy, useless method.
	 */
	public void foo(String str) {
		if ("".equals(str)) {
			for (int i = 0; i < 100; i++) {
				str += i;
			}
		} else {
			if ("foo".equals(str)) {
				for (long x = 1000; x < 100000; x += 10) {
					str += x;
				}
			}
		}

		int y = 7;
		while (y < 10) {
			str += str;
			if ("foobar".equals(str)) {
				if (new Date().toString().equals("202123434")) {
					for (int m = 5; m < 50; m++) {
						System.out.println("print str: " + str);
					}
					if (new Date().getMonth() == 0) {
						if (new Date().getHours() == 11) {
							if (new Date().getDay() == 1) {
								System.out.println("Woo hoo!");
							}
						} else {
							for (int l = 0; l < 5; l++) {
								System.out.println("more");
							}
						}
					}
				} else {
					System.out.println("la la la");
					if (Math.random() == 0d) {
						System.out.println("oh yeah");
					}
				}
			}
			y++;
		}

	}

	/**
	 * Duplicate logic.
	 */
	public void foo2() {
		String str = "";
		int y = 7;
		while (y < 10) {
			str += str;
			if ("foobar".equals(str)) {
				if (new Date().toString().equals("202123434")) {
					for (int m = 5; m < 50; m++) {
						System.out.println("print str: " + str);
					}
					if (new Date().getMonth() == 0) {
						if (new Date().getHours() == 11) {
							if (new Date().getDay() == 1) {
								System.out.println("Woo hoo!");
							}
						} else {
							for (int l = 0; l < 5; l++) {
								System.out.println("more");
							}
						}
					}
				} else {
					System.out.println("la la la");
					if (Math.random() == 0d) {
						System.out.println("oh yeah");
					}
				}
			}
			y++;
		}
	}
}
