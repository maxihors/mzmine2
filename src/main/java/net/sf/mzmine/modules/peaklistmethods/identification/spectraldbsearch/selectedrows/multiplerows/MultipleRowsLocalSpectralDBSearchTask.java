package net.sf.mzmine.modules.peaklistmethods.identification.spectraldbsearch.selectedrows.multiplerows;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import net.sf.mzmine.datamodel.PeakListRow;
import net.sf.mzmine.desktop.Desktop;
import net.sf.mzmine.desktop.impl.HeadLessDesktop;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.peaklistmethods.identification.spectraldbsearch.LocalSpectralDBSearchParameters;
import net.sf.mzmine.modules.visualization.peaklisttable.table.PeakListTable;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.taskcontrol.AbstractTask;
import net.sf.mzmine.taskcontrol.TaskStatus;
import net.sf.mzmine.util.spectraldb.entry.SpectralDBEntry;
import net.sf.mzmine.util.spectraldb.parser.AutoLibraryParser;
import net.sf.mzmine.util.spectraldb.parser.LibraryEntryProcessor;
import net.sf.mzmine.util.spectraldb.parser.UnsupportedFormatException;

public class MultipleRowsLocalSpectralDBSearchTask extends AbstractTask {

  private Logger logger = Logger.getLogger(this.getClass().getName());

  private final PeakListRow[] peakListRows;
  private final @Nonnull String massListName;
  private final File dataBaseFile;

  private ParameterSet parameters;

  private List<MultipleRowsSpectralMatchTask> tasks;

  private PeakListTable table;

  private int totalTasks;

  public MultipleRowsLocalSpectralDBSearchTask(PeakListRow[] peakListRows, PeakListTable table,
      ParameterSet parameters) {
    this.peakListRows = peakListRows;
    this.parameters = parameters;
    this.table = table;
    dataBaseFile = parameters.getParameter(LocalSpectralDBSearchParameters.dataBaseFile).getValue();
    massListName = parameters.getParameter(LocalSpectralDBSearchParameters.massList).getValue();
  }

  /**
   * @see net.sf.mzmine.taskcontrol.Task#getFinishedPercentage()
   */
  @Override
  public double getFinishedPercentage() {
    if (totalTasks == 0 || tasks == null)
      return 0;
    return ((double) totalTasks - tasks.size()) / totalTasks;
  }

  /**
   * @see net.sf.mzmine.taskcontrol.Task#getTaskDescription()
   */
  @Override
  public String getTaskDescription() {
    return "Spectral database identification of " + peakListRows.length
        + " feature lists using database " + dataBaseFile;
  }

  /**
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    setStatus(TaskStatus.PROCESSING);
    int count = 0;

    try {
      tasks = parseFile(dataBaseFile);
      totalTasks = tasks.size();
      if (!tasks.isEmpty()) {
        // wait for the tasks to finish
        while (!isCanceled() && !tasks.isEmpty()) {
          for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).isFinished() || tasks.get(i).isCanceled()) {
              count += tasks.get(i).getCount();
              tasks.remove(i);
              i--;
            }
          }
          // wait for all sub tasks to finish
          try {
            Thread.sleep(100);
          } catch (Exception e) {
            cancel();
          }
        }
        // cancelled
        if (isCanceled()) {
          tasks.stream().forEach(AbstractTask::cancel);
        }
      } else {
        setStatus(TaskStatus.ERROR);
        setErrorMessage("DB file was empty - or error while parsing " + dataBaseFile);
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Could not read file " + dataBaseFile, e);
      setStatus(TaskStatus.ERROR);
      setErrorMessage(e.toString());
    }
    logger.info("Added " + count + " spectral library matches");
    // Repaint the window to reflect the change in the peak list
    Desktop desktop = MZmineCore.getDesktop();
    if (!(desktop instanceof HeadLessDesktop))
      desktop.getMainWindow().repaint();
    // work around to update feature list identities
    if (table.getRowCount() > 0)
      table.setRowSelectionInterval(0, 0);
    setStatus(TaskStatus.FINISHED);

  }

  /**
   * Load all library entries from data base file
   * 
   * @param dataBaseFile
   * @return
   */
  private List<MultipleRowsSpectralMatchTask> parseFile(File dataBaseFile)
      throws UnsupportedFormatException, IOException {
    //
    List<MultipleRowsSpectralMatchTask> tasks = new ArrayList<>();
    AutoLibraryParser parser = new AutoLibraryParser(100, new LibraryEntryProcessor() {
      @Override
      public void processNextEntries(List<SpectralDBEntry> list, int alreadyProcessed) {
        // start last task
        MultipleRowsSpectralMatchTask task =
            new MultipleRowsSpectralMatchTask(peakListRows, parameters, alreadyProcessed + 1, list);
        MZmineCore.getTaskController().addTask(task);
        tasks.add(task);
      }
    });

    // return tasks
    parser.parse(this, dataBaseFile);
    return tasks;
  }

}

