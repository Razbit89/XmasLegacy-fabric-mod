package com.xmaslegacy.menu.render;

import net.minecraft.client.gui.GuiGraphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnowParticleRenderer {
    private static final int MAX_PARTICLES = 150;
    private static final int MAX_STARS = 40;
    
    private static final List<SnowParticle> particles = new ArrayList<>();
    private static final List<TwinklingStar> stars = new ArrayList<>();
    private static final Random random = new Random();
    private static int lastWidth = 0;
    private static int lastHeight = 0;

    public static void init(int width, int height) {
        if (width == lastWidth && height == lastHeight && !particles.isEmpty()) {
            return;
        }
        lastWidth = width;
        lastHeight = height;
        particles.clear();
        stars.clear();

        // Initialize falling snow particles
        for (int i = 0; i < MAX_PARTICLES; i++) {
            double startX = random.nextDouble() * width;
            double startY = random.nextDouble() * height;
            particles.add(createRandomParticle(startX, startY));
        }

        // Initialize twinkling background stars
        for (int i = 0; i < MAX_STARS; i++) {
            double starX = random.nextDouble() * width;
            double starY = random.nextDouble() * (height * 0.7); // limit stars to upper 70% of the screen
            double speed = 0.005 + random.nextDouble() * 0.015; // slow twinkle speed
            stars.add(new TwinklingStar(starX, starY, speed));
        }
    }

    private static SnowParticle createRandomParticle(double x, double y) {
        double speedY = 0.5 + random.nextDouble() * 1.0;
        double speedX = -0.2 + random.nextDouble() * 0.4;
        double size = 1.0 + random.nextDouble() * 3.0;
        float alpha = 0.3f + random.nextFloat() * 0.7f;
        return new SnowParticle(x, y, speedY, speedX, size, alpha);
    }

    public static void render(GuiGraphics guiGraphics, int width, int height) {
        if (particles.isEmpty() || width != lastWidth || height != lastHeight) {
            init(width, height);
        }

        // 1. Draw twinkling stars first (deep background layer)
        for (TwinklingStar star : stars) {
            star.update();
            star.draw(guiGraphics);
        }

        // 2. Draw falling snow particles (foreground layer)
        for (SnowParticle particle : particles) {
            particle.update(width, height);
            particle.draw(guiGraphics);
        }
    }

    private static class TwinklingStar {
        private final double x;
        private final double y;
        private final double speed;
        private float alpha;
        private boolean fadingIn;

        public TwinklingStar(double x, double y, double speed) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.alpha = random.nextFloat();
            this.fadingIn = random.nextBoolean();
        }

        public void update() {
            if (fadingIn) {
                alpha += speed;
                if (alpha >= 1.0f) {
                    alpha = 1.0f;
                    fadingIn = false;
                }
            } else {
                alpha -= speed;
                if (alpha <= 0.1f) {
                    alpha = 0.1f;
                    fadingIn = true;
                }
            }
        }

        public void draw(GuiGraphics guiGraphics) {
            int color = ((int) (this.alpha * 255) << 24) | 0xFFFFFF;
            int ix = (int) this.x;
            int iy = (int) this.y;
            // Draw a 1x1 pixel star
            guiGraphics.fill(ix, iy, ix + 1, iy + 1, color);
        }
    }

    private static class SnowParticle {
        private double x;
        private double y;
        private final double speedY;
        private double speedX;
        private final double size;
        private final float alpha;

        public SnowParticle(double x, double y, double speedY, double speedX, double size, float alpha) {
            this.x = x;
            this.y = y;
            this.speedY = speedY;
            this.speedX = speedX;
            this.size = size;
            this.alpha = alpha;
        }

        public void update(int screenWidth, int screenHeight) {
            this.y += this.speedY;
            this.x += this.speedX;

            if (random.nextDouble() < 0.05) {
                this.speedX += -0.05 + random.nextDouble() * 0.1;
                this.speedX = Math.max(-0.5, Math.min(0.5, this.speedX));
            }

            if (this.y > screenHeight + 5) {
                this.y = -5;
                this.x = random.nextDouble() * screenWidth;
            }
            if (this.x < -5) {
                this.x = screenWidth + 5;
            } else if (this.x > screenWidth + 5) {
                this.x = -5;
            }
        }

        public void draw(GuiGraphics guiGraphics) {
            int color = ((int) (this.alpha * 255) << 24) | 0xFFFFFF;
            int ix = (int) this.x;
            int iy = (int) this.y;
            int isz = (int) this.size;
            
            guiGraphics.fill(ix, iy, ix + isz, iy + isz, color);
        }
    }
}
