package org.moon.figura.animation;

import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.utils.MathUtils;

public enum Interpolation {

    LINEAR((frames, currentFrame, targetFrame, strength, delta, type) -> {
        FiguraVec3 prev = frames[currentFrame].getTargetB();
        FiguraVec3 next = frames[targetFrame].getTargetA();
        FiguraVec3 result = MathUtils.lerp(delta, prev, next);
        return getResult(result, strength, type);
    }),
    CATMULLROM((frames, currentFrame, targetFrame, strength, delta, type) -> {
        FiguraVec3 prevA = frames[Math.max(0, currentFrame - 1)].getTargetB();
        FiguraVec3 prevB = frames[currentFrame].getTargetB();
        FiguraVec3 nextA = frames[targetFrame].getTargetA();
        FiguraVec3 nextB = frames[Math.min(frames.length - 1, targetFrame + 1)].getTargetA();
        FiguraVec3 result = MathUtils.catmullrom(delta, prevA, prevB, nextA, nextB);
        return getResult(result, strength, type);
    }),
    BEZIER((frames, currentFrame, targetFrame, strength, delta, type) -> {
        Keyframe prev = frames[currentFrame];
        Keyframe next = frames[targetFrame];

        FiguraVec3 p0 = prev.getTargetB();
        FiguraVec3 p3 = next.getTargetA();
        FiguraVec3 p1 = p0.plus(prev.getBezierRight());
        FiguraVec3 p2 = p3.plus(next.getBezierLeft());

        FiguraVec3 result = MathUtils.bezier(delta, p0, p1, p2, p3);
        return getResult(result, strength, type);
    }),
    STEP((frames, currentFrame, targetFrame, strength, delta, type) -> getResult(frames[currentFrame].getTargetB().copy(), strength, type));

    private final IInterpolation function;

    Interpolation(IInterpolation function) {
        this.function = function;
    }

    private static FiguraVec3 getResult(FiguraVec3 result, float strength, TransformType type) {
        return type == TransformType.SCALE ? result.offset(-1).scale(strength).offset(1) : result.scale(strength);
    }

    public FiguraVec3 generate(Keyframe[] keyframes, int currentFrame, int targetFrame, float strength, float delta, TransformType type) {
        return this.function.generate(keyframes, currentFrame, targetFrame, strength, delta, type);
    }

    private interface IInterpolation {
        FiguraVec3 generate(Keyframe[] keyframes, int currentFrame, int targetFrame, float strength, float delta, TransformType type);
    }
}
