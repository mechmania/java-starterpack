package com.bot.core;

import com.sun.jna.Structure;
import java.util.List;

@Structure.FieldOrder({"x", "y"})
public class Vec2 extends Structure {

    public float x;
    public float y;

    public Vec2() {}

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // This method is required by JNA for proper array instantiation
    public static class ByValue extends Vec2 implements Structure.ByValue {
        public ByValue() {}
        public ByValue(float x, float y) { super(x, y); }
    }

    public Vec2 add(Vec2 other) {
        return new Vec2(this.x + other.x, this.y + other.y);
    }

    public Vec2 subtract(Vec2 other) {
        return new Vec2(this.x - other.x, this.y - other.y);
    }

    public Vec2 multiply(float scalar) {
        return new Vec2(this.x * scalar, this.y * scalar);
    }

    public Vec2 normalize() {
        double magnitude = Math.sqrt(this.x * this.x + this.y * this.y);
        if (magnitude == 0) {
            return new Vec2(0, 0);
        }
        return new Vec2((float) (this.x / magnitude), (float) (this.y / magnitude));
    }

    public Vec2 rotate(float angleDeg) {
        double angleRad = Math.toRadians(angleDeg);
        double cosA = Math.cos(angleRad);
        double sinA = Math.sin(angleRad);
        float newX = (float) (this.x * cosA - this.y * sinA);
        float newY = (float) (this.x * sinA + this.y * cosA);
        return new Vec2(newX, newY);
    }

    public float dot(Vec2 other) {
        return this.x * other.x + this.y * other.y;
    }

    public float normSq() {
        return this.dot(this);
    }

    public float norm() {
        return (float) Math.sqrt(this.normSq());
    }

    public float theta() {
        return (float) Math.atan2(this.y, this.x);
    }

    public float dist(Vec2 other) {
        return this.subtract(other).norm();
    }

    public float distSq(Vec2 other) {
        return this.subtract(other).normSq();
    }

    @Override
    public String toString() {
        return String.format("Vec2{x=%.2f, y=%.2f}", x, y);
    }
}
