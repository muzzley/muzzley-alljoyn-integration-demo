/******************************************************************************
 * Copyright (c) 2013, AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 ******************************************************************************/

package org.alljoyn.ns.sampleapp;

public enum ColorsEnum {
	Red("#F40303"),
	Green("#04D21C"),
	Blue("#0415D2");
	
	/**
	 * Internal name representation
	 */
	public final String INT_NAME;
	
	/**
	 * Constructor
	 * @param name
	 */
	private ColorsEnum(String name) {
		INT_NAME = name;
	}

	/**
	 * Returns string representation of the enum
	 * @return
	 */
	public static String[] stringValues() {
		ColorsEnum[] enumValues = ColorsEnum.values();
		String[] arr = new String [ enumValues.length ];
		int i = 0;
		for (ColorsEnum ce : enumValues ) {
			arr[i] = ce.toString();
			++i;
		}
		return  arr;
	}//stringValues


}
