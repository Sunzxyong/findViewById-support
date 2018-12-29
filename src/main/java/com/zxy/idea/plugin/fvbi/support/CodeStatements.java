package com.zxy.idea.plugin.fvbi.support;

/**
 * Created by zhengxiaoyong on 2018/10/05.
 */
public class CodeStatements {

    public static final String JAVA_DECLARE_FIELD_EXPRESSION = "%s %s;";

    public static final String JAVA_ASSIGN_VARIABLE_FOR_FINDVIEWBYID_EXPRESSION_BEFORE_API26 = "%s = (%s) %sfindViewById(R.id.%s);";

    public static final String JAVA_ASSIGN_VARIABLE_FOR_FINDVIEWBYID_EXPRESSION_AFTER_API26 = "%s = %sfindViewById(R.id.%s);";

    public static final String JAVA_DECLARE_LOCAL_FOR_FINDVIEWBYID_EXPRESSION_BEFORE_API26 = "%s %s = (%s) %sfindViewById(R.id.%s);";

    public static final String JAVA_DECLARE_LOCAL_FOR_FINDVIEWBYID_EXPRESSION_AFTER_API26 = "%s %s = %sfindViewById(R.id.%s);";

    public static final String KOTLIN_DECLARE_FIELD_EXPRESSION = "lateinit var %s: %s";

    public static final String KOTLIN_ASSIGN_VARIABLE_FOR_FINDVIEWBYID_EXPRESSION_BEFORE_API26 = "%s = %sfindViewById(R.id.%s) as %s";

    public static final String KOTLIN_ASSIGN_VARIABLE_FOR_FINDVIEWBYID_EXPRESSION_AFTER_API26 = "%s = %sfindViewById<%s>(R.id.%s)";

    public static final String KOTLIN_DECLARE_LOCAL_FOR_FINDVIEWBYID_EXPRESSION_BEFORE_API26 = "val %s = %sfindViewById(R.id.%s) as %s";

    public static final String KOTLIN_DECLARE_LOCAL_FOR_FINDVIEWBYID_EXPRESSION_AFTER_API26 = "val %s = %sfindViewById<%s>(R.id.%s)";

    public static final String KOTLIN_FOR_FINDVIEWBYID_EXPRESSION_VARIABLE_LAZY = "val %s by lazy(LazyThreadSafetyMode.NONE) { ";

    public static final String KOTLIN_FOR_FINDVIEWBYID_EXPRESSION_VARIABLE_LAZY_SYNCHRONIZED = "val %s by lazy { ";

    public static final String KOTLIN_FOR_FINDVIEWBYID_EXPRESSION_BEFORE_API26 = "%sfindViewById(R.id.%s) as %s }";

    public static final String KOTLIN_FOR_FINDVIEWBYID_EXPRESSION_AFTER_API26 = "%sfindViewById<%s>(R.id.%s) }";

}
