package de.hda.fbi.db2.stud.impl;

import de.hda.fbi.db2.api.Lab01Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Question;

public class Lab01DataImpl extends Lab01Data {

    List<Question> questions;
    List<Category> categories;

    public void init() {
        questions = new ArrayList<>();
        categories = new ArrayList<>();
    }

    @Override
    public List<?> getQuestions() { return questions; }

    @Override
    public List<?> getCategories() { return categories; }

    @Override
    public void loadCsvFile(List<String[]> csvLines) {
        csvLines.remove(0);
        Map<String, List<Question>> csvCategories = new HashMap<>(10);

        csvLines.forEach(line -> {
            ArrayList<String> choices = new ArrayList<String>();
            choices.add(line[2]);
            choices.add(line[3]);
            choices.add(line[4]);
            choices.add(line[5]);
            Question toAdd = new Question(Integer.parseInt(line[0]), line[1], choices, Integer.parseInt(line[6]));
            var cat = csvCategories.getOrDefault(line[7], new ArrayList<Question>());
            cat.add(toAdd);
            csvCategories.put(line[7], cat);
        });

        csvCategories.forEach((catTitle, catQuestions) -> {
            Category cat = new Category(catTitle);
            catQuestions.forEach(q -> {
                cat.addQuestion(q);
                questions.add(q);
            });
            categories.add(cat);

        });
        System.out.println("Anzahl Kategorien: " + this.categories.size() + "\nAnzahl Fragen: " + this.questions.size());
    }
}
