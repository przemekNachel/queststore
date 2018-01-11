import java.util.Scanner;

class ArtifactStoreView{
    public static String artifactNameQuestion{
      Scanner sc = new Scanner(System.in);
      System.out.println("Name your artifact: ");
      return sc.next();
    }

    public static String artifactPriceQuestion{
      Scanner sc = new Scanner(System.in);
      System.out.println("How much should an artifact cost: ")
      return sc.nextFloat()
    }

    public static String artifactDescriptionQuestion{
      Scanner sc = new Scanner(System.in);
      System.out.println("Add description: ")
      return sc.next();
    }

    public String artifactPurchaseQuestion{
    }

    public String artifactCategoryQuestion{
    }

    public String artifacUnavaliable{
    }

    public String insufficientFunds{
    }
}
