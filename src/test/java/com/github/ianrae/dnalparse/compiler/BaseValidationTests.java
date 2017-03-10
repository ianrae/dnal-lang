package com.github.ianrae.dnalparse.compiler;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.dnal.compiler.dnalgenerate.ASTToDNALGenerator;
import org.dnal.compiler.dnalgenerate.CustomRuleFactory;
import org.dnal.compiler.nrule.StandardRuleFactory;
import org.dnal.compiler.parser.FullParser;
import org.dnal.compiler.parser.ast.Exp;
import org.dnal.compiler.validate.ValidationPhase;
import org.dnal.core.DType;
import org.dnal.core.DTypeRegistry;
import org.dnal.core.DValue;
import org.dnal.core.NewErrorMessage;
import org.dnal.core.ValidationState;
import org.dnal.core.repository.World;
import org.junit.Before;

public class BaseValidationTests extends BaseTest {
    
    @Before
    public void init() {
        StandardRuleFactory rf = new StandardRuleFactory();
        crf = rf.createFactory();
    }
    
    protected void chkInvalid(DValue dval) {
        assertEquals(ValidationState.INVALID, dval.getValState());
    }


    protected void log(String s) {
        System.out.println(s);
    }
    
    protected void parseAndValidate(String input, boolean expected, String shape) {
        List<NewErrorMessage> errL = new ArrayList<>();
        ASTToDNALGenerator dnalGenerator = parse(errL, input, "Foo", shape, crf);
        errL.addAll(dnalGenerator.getErrL());
        World world = getContext().world;
        ValidationPhase validator = new ValidationPhase(world, getContext().et);
    
//      DType type = dnalGenerator.getRegistry().getType("Foo");
//      for(NRule rule: CustomRuleRegistry.getRuleRunners()) {
//          type.getRawRules().add(rule);
//      }
        boolean b = validator.validate();
        validator.dumpErrors();
        assertEquals(expected, b);
    }
    
    protected int expected = 2;
    protected boolean generateOk = true;
    protected ASTToDNALGenerator parse(List<NewErrorMessage> errL, String input, String typeName, String baseType, CustomRuleFactory crf) {
        log("doing: " + input);
        List<Exp> list = FullParser.fullParse(input);
        assertEquals(expected, list.size());
        ASTToDNALGenerator dnalGenerator = createASTGenerator();
        boolean b = dnalGenerator.generate(list);
        dnalGenerator.dumpErrors();
        assertEquals(generateOk, b);

        World world = getContext().world;
        world.dump();

        DTypeRegistry registry = getContext().registry;
        DType type = registry.getType(typeName);
        assertEquals(typeName, type.getName());

        if (baseType == null) {
            assertEquals(null, type.getBaseType());
        } else {
            assertEquals(baseType, type.getBaseType().getName());
        }
        return dnalGenerator;
    }
}
