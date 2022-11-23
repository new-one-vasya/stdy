package nw.one.vasya.lesson5;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {


    public static void main(String[] args) {

        NameHolder holder = new NameHolder();

        Named root = new Named("root");
        generateSubNamed(root, "r");

        Named leafOne = new Named("leafOne");
        generateSubNamed(leafOne, "mm");

        Named leafTwo = new Named("leafTwo");
        generateSubNamed(leafTwo, "tt");

        Named tearThree = new Named("tearThree");
        generateSubNamed(tearThree, "dd");
        leafTwo.setNamed(List.of(tearThree));

        root.setNamed(List.of(leafOne, leafTwo));
        holder.setNamed(root);

        System.out.println(holder);

//        Optional.ofNullable(holder)
//                .map(holder.getNamed())
//                .stream().mapMulti()


        List<Integer> ints = new Random().ints(0, 100)
                .filter(it -> it < 10)
                .limit(10)
//                .map(it -> Integer.valueOf(it))
                .boxed() // оборачиваем, так как коллекции не работают с примитивами
                .reduce(new ArrayList<>(),
                        (x, y) -> {
                            x.add(y);
                            return x;
                        },
                        (a, b) -> {
                            a.addAll(b);
                            return a;
                        });

//        System.out.println(ints);

//        new Random().ints(100)
//                .filter(it -> it < 10)
//                .limit(10)
//                .forEach(System.out::println);


        List<String> expand = expand(holder);
        System.out.println("1 --- " + expand);

        ArrayList<String> reduce = Optional.ofNullable(holder.getNamed())
                .stream()
                .reduce(new ArrayList<>(),
                        (hold, value) -> {
                            hold.addAll(expandpl(value));
                            return hold;
                        },
                        (hold, prom) -> {
                            hold.addAll(prom);
                            return hold;
                        });
        System.out.println("2 --- " + reduce);

        // тут стоит начать со стрима из одного элемента, чтобы не потреять первые вложенные строки
        Stream<Object> objectStream = Stream.of(holder.getNamed()).mapMulti(Main::expandIterable2);
        System.out.println("-------------------------");
//        objectStream.forEach(System.out::println);
        System.out.println("3 --- " + objectStream.toList());


//        var nestedList = List.of(1, List.of(2, List.of(3, 4)), 5);
//        Stream<Object> expandedStream = nestedList.stream().mapMulti(Main::expandIterable);
//        expandedStream.forEach(System.out::println);

    }

    static void expandIterable2(Object e, Consumer<Object> c) {
        System.out.println("-> " + e);
        if (e instanceof Iterable<?> elements) {
            for (Object ie : elements) {
                if (ie instanceof Named nam) {
                    expandIterable2(nam.getNamed(), c);
                    expandIterable2(nam.getSubNamed(), c);
                } else {
                    expandIterable2(ie, c);
                }
            }
        } else if (e instanceof Named nam) {
            expandIterable2(nam.getNamed(), c);
            expandIterable2(nam.getSubNamed(), c);
        } else if (e instanceof SubNamed snam) {
            expandIterable(snam.getName(), c);
        }
        else if (e != null) {
            c.accept(e);
        }
    }

    static void expandIterable(Object e, Consumer<Object> c) {
        if (e instanceof Iterable<?> elements) {
            for (Object ie : elements) {
                expandIterable(ie, c);
            }
        } else if (e != null) {
            c.accept(e);
        }
    }

    private static List<String> expandpl(Named value) {
        List<String> list = new ArrayList<>();

        if (value == null) {
            return list;
        }
        list.addAll(expandspl(value.getNamed()));
        list.addAll(expandpl(value.getSubNamed()));

        return list;
    }

    private static Collection<String> expandspl(List<Named> named) {
        List<String> list = new ArrayList<>();

        if (named == null) {
            return list;
        }
        for (Named value : named) {
            list.addAll(expandpl(value));
        }
        return list;
    }

    private static List<String> expandpl(List<SubNamed> subNameds) {
        List<String> list = new ArrayList<>();

        if (subNameds == null) {
            return list;
        }
        for (SubNamed value : subNameds) {
            list.add(value.getName());
        }

        return list;
    }

    private static List<String> expand(NameHolder holder) {
        List<String> list = new ArrayList<>();

        Named holderNamed = holder.getNamed();
        if (holderNamed != null) {
            expands(list, holderNamed.getNamed());
            expand(list, holderNamed.getSubNamed());
        }

        return list;
    }

    private static void expands(final List<String> list, List<Named> named) {
        if (named == null) {
            return;
        }

        for (Named record : named) {
            expands(list, record.getNamed());
            expand(list, record.getSubNamed());
        }

    }

    private static void expand(final List<String> list, List<SubNamed> named) {
        Optional.ofNullable(named)
                .stream()
                .flatMap(List::stream)
                .forEach(it -> list.add(it.getName()));
    }

    private static void generateSubNamed(Named node, String sub) {
        int count = 0;
        node.setSubNamed(List.of(new SubNamed(sub + ++count), new SubNamed(sub + ++count)));
    }

    public static void doIt() {
        List<Log> logs = List.of(
                new Log("Сибирская сосна", 10),
                new Log("Дуб монгольский", 30),
                new Log("Берёза карликовая", 5));

        logs
                .stream()
                .map(Log::getType)
                .map(x -> x.split(" "))
                .flatMap(Arrays::stream)
                .map(String::chars)
                .flatMapToInt(x -> x)
                .forEach(x -> System.out.println((char) x));

    }

    static class Log {
        String type;
        int count;

        public Log(String type, int count) {
            this.type = type;
            this.count = count;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
        // конструктор и гетеры опущены
    }

}
