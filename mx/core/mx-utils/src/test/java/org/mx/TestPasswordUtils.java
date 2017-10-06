package org.mx;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mx.PasswordUtils.PasswordStrengthen;

public class TestPasswordUtils {

	@Test
	public void testPasswordStrengthen() {
		 assertEquals(PasswordStrengthen.LOW,
		 PasswordUtils.checkStrengthen(null));
		 assertEquals(PasswordStrengthen.LOW,
		 PasswordUtils.checkStrengthen(""));
		 assertEquals(PasswordStrengthen.LOW, PasswordUtils.checkStrengthen("		 "));
		 assertEquals(PasswordStrengthen.LOW,
		 PasswordUtils.checkStrengthen("abcd"));
		 assertEquals(PasswordStrengthen.LOW,
		 PasswordUtils.checkStrengthen("abcdefghij"));
		 assertEquals(PasswordStrengthen.LOW,
		 PasswordUtils.checkStrengthen("ABCD"));
		 assertEquals(PasswordStrengthen.LOW,
		 PasswordUtils.checkStrengthen("123456"));
		 assertEquals(PasswordStrengthen.LOW,
		 PasswordUtils.checkStrengthen("!@#$"));
		
		 assertEquals(PasswordStrengthen.MEDIUM,
		 PasswordUtils.checkStrengthen("abcdefgAB"));
		 assertEquals(PasswordStrengthen.MEDIUM,
		 PasswordUtils.checkStrengthen("124asdfas"));
		 assertEquals(PasswordStrengthen.MEDIUM,
		 PasswordUtils.checkStrengthen("SDSDKJ345234"));
		 assertEquals(PasswordStrengthen.MEDIUM,
		 PasswordUtils.checkStrengthen("345345@#$"));
		 assertEquals(PasswordStrengthen.MEDIUM,
		 PasswordUtils.checkStrengthen("sdasd&^%%"));
		 assertEquals(PasswordStrengthen.MEDIUM,
		 PasswordUtils.checkStrengthen("JHJD&^%$"));
		
		 assertEquals(PasswordStrengthen.HIGH,
		 PasswordUtils.checkStrengthen("asdlkj2342JHD"));
		 assertEquals(PasswordStrengthen.HIGH,
		 PasswordUtils.checkStrengthen("askdjfDJFD&^#@"));
		 assertEquals(PasswordStrengthen.HIGH,
		 PasswordUtils.checkStrengthen("23423DJHF!@#"));
		 assertEquals(PasswordStrengthen.HIGH,
		 PasswordUtils.checkStrengthen("skdjfsk345345$#%^"));
		
		 assertEquals(PasswordStrengthen.HIGHER,
		 PasswordUtils.checkStrengthen("asdkfj234627JDFJ@$#$"));
	}
}
