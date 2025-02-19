package com.ruyiruyi.rylibrary.utils;

import static java.lang.Math.*;

public class GetAngleAndDistance {

    public static void main(String[] args) {

        double[] a = CalculateBraring(116.37047010, 39.83594606, 116.42088584, 39.87221456);
        for (double v : a) {
            System.out.println(v);
        }
    }
        public static double judge ( double brng){
            if (brng < 0) {
                return brng + 360;
            } else {
                return brng;
            }

        }

        //WGS84坐标系，长轴6378137.000m，短轴6356752.314，扁率1/298.257223563。
        public static final double f = 1 /  298.257223563;
        public static final double a= 6378137.0;
        public static final double b= 6356752.314245;

        public static double[] CalculateBraring(double lon1, double lat1, double lon2, double lat2) {
            //将经纬度坐标转换为弧度
            lon1 = Math.toRadians(lon1);
            lat1 = Math.toRadians(lat1);
            lon2 = Math.toRadians(lon2);
            lat2 = Math.toRadians(lat2);

            double L = lon2 - lon1;

            double tanU1 = (1 - f) * tan(lat1);
            double cosU1 = 1 / sqrt((1 + tanU1 * tanU1));
            double sinU1 = tanU1 * cosU1;

            double tanU2 = (1 - f) * tan(lat2);
            double cosU2 = 1 / sqrt((1 + tanU2 * tanU2));
            double sinU2 = tanU2 * cosU2;

            double lambda = L;
            double lambda_ = 0;
            double iterationLimit = 100;

            double cosSq_alpha = 0;

            double sin_delta = 0;
            double cos2_deltaM = 0;
            double cos_delta = 0;
            double delta = 0;
            double sinlambda = 0;
            double coslambda = 0;
            double s = 0;

            while (abs(lambda - lambda_) > 1e-12 && iterationLimit > 0) {
                iterationLimit = iterationLimit - 1;
                sinlambda = sin(lambda);
                coslambda = cos(lambda);
                double sinSq_delta = (cosU2 * sinlambda) * (cosU2 * sinlambda) + (cosU1 * sinU2 - sinU1 * cosU2 * coslambda) * (cosU1 * sinU2 - sinU1 * cosU2 * coslambda);
                sin_delta = sqrt(sinSq_delta);
                if (sin_delta == 0) {

                }
                cos_delta = sinU1 * sinU2 + cosU1 * cosU2 * coslambda;
                delta = atan2(sin_delta, cos_delta);
                double sin_alpha = cosU1 * cosU2 * sinlambda / sin_delta;
                cosSq_alpha = 1 - sin_alpha * sin_alpha;
                cos2_deltaM = cos_delta - 2 * sinU1 * sinU2 / cosSq_alpha;
                double C = f / 16 * cosSq_alpha * (4 + f * (4 - 3 * cosSq_alpha));
                lambda_ = lambda;
                lambda = L + (1 - C) * f * sin_alpha * (delta + C * sin_delta * (cos2_deltaM + C * cos_delta * (-1 + 2 * cos2_deltaM * cos2_deltaM)));
            }

            double uSq = cosSq_alpha * (a * a - b * b) / (b * b);
            double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
            double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
            double delta_delta = B * sin_delta * (cos2_deltaM + B / 4 * (cos_delta * (-1 + 2 * cos2_deltaM * cos2_deltaM) - B / 6 * cos2_deltaM * (-3 + 4 * sin_delta * sin_delta) * (-3 + 4 * cos2_deltaM * cos2_deltaM)));
            s = b * A * (delta - delta_delta);                                        //距离m
            double fwdAz = atan2(cosU2 * sinlambda, cosU1 * sinU2 - sinU1 * cosU2 * coslambda); //初始方位角
            double revAz = atan2(cosU1 * sinlambda, -sinU1 * cosU2 + cosU1 * sinU2 * coslambda); //最终方位角
            double fwdAz_ = judge(Math.toDegrees(fwdAz));  //将角度归一化至0 - 360°内
            double revAz_ = judge(Math.toDegrees(revAz));  //将角度归一化至0 - 360°内
            s = s / 1000;  //距离km

            double[] coordinates = new double[3];

            coordinates[0] = fwdAz_;
            coordinates[1] = revAz_;
            coordinates[2] = s;

            return coordinates;
        }

    }

