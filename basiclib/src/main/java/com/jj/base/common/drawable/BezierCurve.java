
package com.jj.base.common.drawable;


public class BezierCurve {

    private float[] mSamplePt;

    public BezierCurve(float[] ctrl_points) {
        this(ctrl_points, 300);
    }

    public BezierCurve(float[] ctrl_points, int sample_count) {
        mSamplePt = new float[sample_count * 2];

        Init(ctrl_points);
    }

    public float value_x(float v) {
        int pt_num = mSamplePt.length / 2;
        int i = (int) (v * pt_num + 0.5);

        if (i < 0)
            i = 0;
        else if (i >= pt_num)
            i = pt_num - 1;
      
        return mSamplePt[i * 2];
    }

    public float value_y(float v) {
        int pt_num = mSamplePt.length / 2;
        int i = (int) (v * pt_num + 0.5);

        if (i < 0)
            i = 0;
        else if (i >= pt_num)
            i = pt_num - 1;
        return mSamplePt[i * 2 + 1];
    }

    private void Init(float[] ctrl_points) {
        final int length = ctrl_points.length;
        if (length % 2 != 0 || length < 2)
            throw new IllegalArgumentException("error in BezierCurve::Init");

        final int count = length / 2;
        float[] px = new float[count];
        float[] py = new float[count];
        for (int i = 0, j = 0; i < length; i += 2, j++) {
            px[j] = ctrl_points[i];
            py[j] = ctrl_points[i + 1];
        }

        // initCoefficient 的ctrl_points和coefficient可以是同一个数组
        float[] coefficientX = px;
        initCoefficient(px, coefficientX, count);
        float[] coefficientY = py;
        initCoefficient(py, coefficientY, count);

        final float[] samplePt = this.mSamplePt;
        samplePt[0] = coefficientX[0];
        samplePt[1] = coefficientY[0];

        float[] a = new float[count];
        final int samplePtLength = samplePt.length;
        final float samplePtLengthF = samplePtLength;
        for (int i = 2; i < samplePtLength; i += 2) {
            float v = i / samplePtLengthF;

            final int n = count - 1;
            float v1 = v / (1 - v);
            a[0] = (float) Math.pow(1 - v, n);
            for (int j = 1; j < count; j++) {
                a[j] = a[j - 1] * v1;
            }
            samplePt[i] = sumProduct(coefficientX, a, count);
            samplePt[i + 1] = sumProduct(coefficientY, a, count);
        }

    }

    public static void initCoefficient(float[] ctrl_points, float[] coefficient, final int length) {
        coefficient[0] = ctrl_points[0];
        for (int i = 1, Cni = 1; i < length; i++) {
            Cni = Cni * (length - i) / i;
            coefficient[i] = ctrl_points[i] * Cni;
        }
    }

    public static float sumProduct(float[] a, float[] b, final int count) {
        float ret = 0;
        for (int i = 0; i < count; i++) {
            ret += a[i] * b[i];
        }
        return ret;
    }
}
