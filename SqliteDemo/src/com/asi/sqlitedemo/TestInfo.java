package com.asi.sqlitedemo;

import com.asi.notedatabase.Column;
import com.asi.notedatabase.InTable;

/**
 * Created by asizhou on 2015/4/24.
 */
@InTable(name = "TestInfo", version = 0)
public class TestInfo {
    //test all data type
    @Column
    public boolean      testboolean;
    @Column
    public Boolean      testBoolean_;
    @Column
    public char         testChar;
    @Column
    public Character    testCharacter;
    @Column
    public short        testshort;
    @Column
    public Short        testShort_;
    @Column(primaryKey = true)
    public int          testInt;
    @Column
    public Integer      testInteger;
    @Column
    public long         testLong;
    @Column
    public Long         testLong_;
    @Column
    public double       testdouble;
    @Column
    public Double       testDouble_;
    @Column
    public float        testfloat;
    @Column
    public Float        testFloat_;
    @Column
    public String       testString;

    //not in table
    public int          testOther;

    public TestInfo() {}
    public TestInfo(boolean b, Boolean B, char c, Character C, short s, Short S, int i, Integer I, long l, Long L, double d, Double D, float f, Float F, String St) {
        testboolean     = b;
        testBoolean_    = B;
        testChar        = c;
        testCharacter   = C;
        testshort       = s;
        testShort_      = S;
        testInt         = i;
        testInteger     = I;
        testLong        = l;
        testLong_       = L;
        testdouble      = d;
        testDouble_     = D;
        testfloat       = f;
        testFloat_      = F;
        testString      = St;
    }
}
