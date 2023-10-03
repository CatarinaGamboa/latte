package typechecking;

import java.util.ArrayList;
import java.util.List;

import context.Context;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;

public class UniquenessProcessor extends AbstractProcessor<CtPackage> {

    List<CtPackage> visitedPackages = new ArrayList<>();
    Factory factory;

    public UniquenessProcessor(Factory factory) {
        this.factory = factory;
    }

    @Override
    public void process(CtPackage pkg) {
//        if (!visitedPackages.contains(pkg)) {
//            visitedPackages.add(pkg);
//            Context c = Context.getInstance();
//            c.reinitializeAllContext();
//
//            pkg.accept(new LatteTypeChecker(c, factory));
//
//        }

    }
    
}