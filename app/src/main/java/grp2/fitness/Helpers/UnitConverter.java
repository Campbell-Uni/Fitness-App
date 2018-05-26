package grp2.fitness.Helpers;

public class UnitConverter {

    public enum Gender {
        MALE(5),
        FEMALE(-161);

        private int value;
        Gender(int value){
            this.value = value;
        }

        public int getValue(){
            return value;
        }
    }

    public enum PhysicalActivity {
        SEDENTARY(1.2),
        MODERATE(1.3),
        ACTIVE(1.4);

        private double value;
        PhysicalActivity(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }
    }

    public static double getKjFromCal(double calories){
        return calories * 0.239006;
    }

    public static double getCalFromKj(double kj){
        return kj * 0.239006;
    }

    public static double getBMI(double weightInKg, double heightInMetres){
        return weightInKg / Math.pow(heightInMetres, 2);
    }

    //Mifflin-St. Jeor Equation
    public static double getRequiredKj(Gender gender, PhysicalActivity physicalActivity, double weightInKg, double heightInCentimetres, double ageInYears){
        return getKjFromCal((10 * weightInKg + 6.25 * heightInCentimetres - 5 * ageInYears + gender.getValue()) * physicalActivity.getValue());
    }
}
