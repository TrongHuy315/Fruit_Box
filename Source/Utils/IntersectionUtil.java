package Source.Utils;

import javafx.geometry.Bounds;
import javafx.geometry.BoundingBox;

public class IntersectionUtil {
    public static Bounds getIntersection(Bounds bounds1, Bounds bounds2) {
        if (bounds1 == null || bounds2 == null || !bounds1.intersects(bounds2)) {
            return null;
        }

        double intersectMinX = Math.max(bounds1.getMinX(), bounds2.getMinX());
        double intersectMinY = Math.max(bounds1.getMinY(), bounds2.getMinY());
        double intersectMaxX = Math.min(bounds1.getMaxX(), bounds2.getMaxX());
        double intersectMaxY = Math.min(bounds1.getMaxY(), bounds2.getMaxY());

        double intersectWidth = intersectMaxX - intersectMinX;
        double intersectHeight = intersectMaxY - intersectMinY;

        if (intersectWidth <= 0 || intersectHeight <= 0) {
            return null;
        }

        return new BoundingBox(intersectMinX, intersectMinY, intersectWidth, intersectHeight);
    }

    public static double getArea(Bounds bounds) {
        if (bounds == null || bounds.getWidth() <= 0 || bounds.getHeight() <= 0) {
            return 0.0;
        }
        return bounds.getWidth() * bounds.getHeight();
    }

    public static double getOverlapPercentageOfFirst(Bounds bounds1, Bounds bounds2) {
        Bounds intersection = getIntersection(bounds1, bounds2);
        if (intersection == null) {
            return 0.0;
        }

        double area1 = getArea(bounds1);
        if (area1 == 0.0) {
            return 0.0;
        }

        double areaIntersect = getArea(intersection);
        return (areaIntersect / area1) * 100.0;
    }

    public static double getOverlapPercentageOfSecond(Bounds bounds1, Bounds bounds2) {
        return getOverlapPercentageOfFirst(bounds2, bounds1);
    }

    public static double getOverlapPercentageBasedOnSmaller(Bounds bounds1, Bounds bounds2) {
        Bounds intersection = getIntersection(bounds1, bounds2);
        if (intersection == null) {
            return 0.0;
        }

        double area1 = getArea(bounds1);
        double area2 = getArea(bounds2);
        double areaIntersect = getArea(intersection);

        if (area1 == 0.0 || area2 == 0.0) {
            return 0.0;
        }

        double minArea = Math.min(area1, area2);
        return (areaIntersect / minArea) * 100.0;
    }

    public static double getJaccardIndexPercentage(Bounds bounds1, Bounds bounds2) {
        Bounds intersection = getIntersection(bounds1, bounds2);

        double area1 = getArea(bounds1);
        double area2 = getArea(bounds2);
        double areaIntersect = getArea(intersection);

        double areaUnion = area1 + area2 - areaIntersect;
        if (areaUnion == 0.0) {
            return 0.0;
        }

        return (areaIntersect / areaUnion) * 100.0;
    }
}
