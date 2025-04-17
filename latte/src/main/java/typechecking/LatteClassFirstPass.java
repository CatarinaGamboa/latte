package typechecking;

import context.ClassLevelMaps;
import context.PermissionEnvironment;
import context.SymbolicEnvironment;
import context.UniquenessAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;

public class LatteClassFirstPass extends LatteAbstractChecker{

    public LatteClassFirstPass(SymbolicEnvironment se, PermissionEnvironment pe,
            ClassLevelMaps mtc) {
        super(se, pe, mtc);
        logInfo("[ First Class Pass started ]");
        enterScopes();
    }

    @Override
    public <T> void visitCtClass(CtClass<T> ctClass) {
		logInfo("Visiting class: " + ctClass.getSimpleName());
		// Add the class to the type reference and class map
		CtTypeReference<?> typeRef = ctClass.getReference();
		maps.addTypeClass(typeRef, ctClass);
		super.visitCtClass(ctClass);
	}
			
			
	@Override
	public <T> void visitCtField(CtField<T> f) {
		logInfo("Visiting field: " + f.getSimpleName());
		loggingSpaces++;
		CtElement k = f.getParent();
		if (k instanceof CtClass){
			CtClass<?> klass = (CtClass<?>) k;
			maps.addFieldClass(f, klass);
			logInfo(String.format("Added field %s to class %s in the mappings", f.getSimpleName(), klass.getSimpleName()));

			UniquenessAnnotation ua = maps.getFieldAnnotation(f.getSimpleName(), klass.getReference());
			logInfo(String.format("Field %s has annotation %s in mapping", f.getSimpleName(), ua));

		} else {
			logError(String.format("Field %s is not inside a class", f.getSimpleName()), f);
		}

		super.visitCtField(f);
		loggingSpaces--;
	}


	@Override
	public <T> void visitCtMethod(CtMethod<T> m) {
		logInfo("Visiting method: " + m.getSimpleName());
		maps.addMethod((CtClass<?>) m.getParent(), m);
		super.visitCtMethod(m);
	}

	@Override
	public <T> void visitCtConstructor(CtConstructor<T> c) {
		logInfo("Visiting constructor: " + c.getSimpleName());
		maps.addConstructor((CtClass<?>) c.getParent(), c);
		super.visitCtConstructor(c);
	}


}
