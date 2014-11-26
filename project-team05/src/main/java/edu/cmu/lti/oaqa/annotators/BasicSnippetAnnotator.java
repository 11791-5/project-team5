package edu.cmu.lti.oaqa.annotators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import json.gson.Snippet;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringList;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;

import util.FullDocumentSources;
import util.SimilarityMeasures;
import util.Utils;
import edu.cmu.lti.oaqa.type.input.ExpandedQuestion;
import edu.cmu.lti.oaqa.type.retrieval.Passage;
import edu.cmu.lti.oaqa.type.retrieval.SnippetSearchResult;
import edu.cmu.lti.oaqa.type.retrieval.SynSet;
import edu.cmu.lti.oaqa.type.retrieval.Synonym;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;

public class BasicSnippetAnnotator extends SnippetAnnotator {

}