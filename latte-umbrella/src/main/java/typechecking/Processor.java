package typechecking;

import java.util.ArrayList;
import java.util.List;

import context.ClassLevelMaps;
import context.Context;
import context.PermissionEnvironment;
import context.SymbolicEnvironment;
import context.TypeEnvironment;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;

public class Processor extends AbstractProcessor<CtPackage> {

    List<CtPackage> visitedPackages = new ArrayList<>();
    Factory factory;

    public Processor(Factory factory) {
        this.factory = factory;
    }

    @Override
    public void process(CtPackage pkg) {
        if (!visitedPackages.contains(pkg)) {
            visitedPackages.add(pkg);
            Context c = Context.getInstance();
            TypeEnvironment te = TypeEnvironment.getInstance();
            SymbolicEnvironment se = SymbolicEnvironment.getInstance();
            PermissionEnvironment pe = PermissionEnvironment.getInstance();
            ClassLevelMaps mtc = ClassLevelMaps.getInstance();
            c.reinitializeAllContext();

            pkg.accept(new LatteClassFirstPass(c, te, se, pe, mtc));
            pkg.accept(new LatteTypeChecker(c, te, se, pe, mtc));

        }

    }
    
}