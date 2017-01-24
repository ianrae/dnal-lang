package com.github.ianrae.world;

import static org.junit.Assert.assertNotNull;

import org.dnal.compiler.dnalgenerate.RuleDeclaration;
import org.dnal.compiler.dnalgenerate.RuleFactory;
import org.dnal.compiler.impoter.MockImportLoader;
import org.dnal.compiler.nrule.Custom1Rule;
import org.dnal.compiler.nrule.NeedsCustomRule;
import org.dnal.compiler.parser.ast.CustomRule;
import org.dnal.core.DValue;
import org.dnal.core.Shape;
import org.dnal.core.nrule.NRule;
import org.dnal.core.nrule.NRuleContext;
import org.dnal.core.nrule.virtual.VirtualString;
import org.junit.Test;

import com.github.ianrae.dnalparse.DNALCompiler;
import com.github.ianrae.dnalparse.DataSet;

public class WorldCustomRuleTests extends BaseWorldTest {
    
    public static class SentenceRule extends Custom1Rule<VirtualString> implements NeedsCustomRule { 
        public static final String NAME = "isSentence";
        
        public SentenceRule() {
            super(NAME, new VirtualString());
        }

        @Override
        protected boolean onEval(DValue dval, NRuleContext ctx) {
            return arg1.val.endsWith(".");
        }

        @Override
        public void rememberCustomRule(CustomRule exp) {
            this.polarity = exp.polarity;
        }
        
        public static class Factory implements RuleFactory {
            @Override
            public NRule createRule(String ruleName, Shape shape) {
                SentenceRule rule = new SentenceRule();
                return rule;
            }

            @Override
            public RuleDeclaration getDeclaration() {
                RuleDeclaration decl = new RuleDeclaration(NAME, Shape.STRING);
                return decl;
            }
        }
    }    
    
    @Test
    public void test() {
        String s = String.format("rule isSentence string; type Foo string isSentence() end let x Foo = 'abc.' let y Foo = 'def.'");
        DNALCompiler compiler = createCompiler();
        compiler.registryRuleFactory(new SentenceRule.Factory());
        DataSet dataSet = compiler.compileString(s);
        assertNotNull(dataSet);
    }
    
    
}
