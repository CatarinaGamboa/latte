package typechecking;

import context.ClassLevelMaps;
import context.Context;
import context.PermissionEnvironment;
import context.SymbolicEnvironment;
import context.TypeEnvironment;
import context.UniquenessAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtTypeReference;

public class LatteClassFirstPass extends LatteProcessor{

    public LatteClassFirstPass(Context c, TypeEnvironment te, SymbolicEnvironment se, PermissionEnvironment pe,
            ClassLevelMaps mtc) {
        super(c, te, se, pe, mtc);
        logInfo("[ First Class Pass started ]");
        enterScopes();
    }

    @Override
    public <T> void visitCtClass(CtClass<T> ctClass) {
		logInfo("Visiting class: " + ctClass.getSimpleName());
		context.addClass(ctClass);
		enterScopes();

		// Add the class to the type reference and class map
		CtTypeReference<?> typeRef = ctClass.getReference();
		maps.addTypeClass(typeRef, ctClass);
		super.visitCtClass(ctClass);

		exitScopes();
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
			logError(String.format("Field %s is not inside a class", f.getSimpleName()));
		}

		super.visitCtField(f);
		loggingSpaces--;
	}



}
