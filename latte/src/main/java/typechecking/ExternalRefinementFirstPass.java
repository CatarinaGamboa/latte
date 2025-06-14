package typechecking;

import context.ClassLevelMaps;
import context.PermissionEnvironment;
import context.SymbolicEnvironment;
import context.UniquenessAnnotation;
import specification.ExternalRefinementsFor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * First pass that collects permission annotations (@unique, @shared, etc.)
 * for external method parameters and stores them in ClassLevelMaps.
 */
public class ExternalRefinementFirstPass extends LatteAbstractChecker {

    public ExternalRefinementFirstPass(SymbolicEnvironment symbEnv,
                                       PermissionEnvironment permEnv,
                                       ClassLevelMaps maps) {
        super(symbEnv, permEnv, maps);
    }

    @Override
    public <T> void visitCtInterface(CtInterface<T> ctInterface) {
        // @ExternalRefinementsFor annotation check
        boolean hasAnnotation = ctInterface.getAnnotations().stream().anyMatch(ann ->
                ann.getAnnotationType().getQualifiedName().equals("specification.ExternalRefinementsFor")
        );

        if (!hasAnnotation) {
            return;
        }

        CtTypeReference<?> targetRef = null;
        for (CtAnnotation<? extends Annotation> annotation : ctInterface.getAnnotations()) {
            if (annotation.getAnnotationType().getQualifiedName().equals("specification.ExternalRefinementsFor")) {
                CtExpression<?> expr = annotation.getValues().get("value");

                if (expr instanceof CtLiteral<?> && ((CtLiteral<?>) expr).getValue() instanceof String) {
                    targetRef = ctInterface.getFactory().Type().createReference((String) ((CtLiteral<?>) expr).getValue());
                } else {
                    logWarning("Expected a string literal in @ExternalRefinementsFor");
                    return;
                }

                break;
            }
        }

        if (targetRef == null) {
            logWarning("No target class specified in @ExternalRefinementsFor");
            return;
        }

        if (ctInterface.isPublic()) {
            logInfo("Processing external interface: " + ctInterface.getQualifiedName(), ctInterface);
            scan(ctInterface.getMethods());
        }
    }

    @Override
    public <T> void visitCtMethod(CtMethod<T> method) {
        CtTypeReference<?> declaringClass = method.getDeclaringType().getReference();

        // Skip if it has a body — it's not external
        CtBlock<?> body = method.getBody();
        if (body != null && !body.isImplicit()) return;

        List<CtParameter<?>> parameters = method.getParameters();
        List<UniquenessAnnotation> paramAnnotations = new ArrayList<>();

        for (CtParameter<?> param : parameters) {
            UniquenessAnnotation ua = new UniquenessAnnotation(param);
            paramAnnotations.add(ua);
        }

        Pair<String, Integer> methodSig = Pair.of(method.getSimpleName(), parameters.size());

        maps.addExternalMethodParamPermissions(declaringClass, methodSig.getLeft(), methodSig.getRight(), paramAnnotations);

        logInfo("Collected annotations for method: " + methodSig + " => " + paramAnnotations, method);
    }
}