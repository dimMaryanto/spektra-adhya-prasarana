package app.controller.peminjaman.karyawan;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.dialog.ExceptionDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import app.configs.BootInitializable;
import app.configs.DialogsFX;
import app.configs.StringFormatterFactory;
import app.entities.kepegawaian.KasbonKaryawan;
import app.entities.master.DataKaryawan;
import app.repositories.RepositoryKaryawan;
import app.repositories.RepositoryKasbonKaryawan;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

@Component
public class KasbonKaryawanListController implements BootInitializable {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private ApplicationContext springContext;

	@Autowired
	private RepositoryKaryawan serviceKaryawan;

	@Autowired
	private RepositoryKasbonKaryawan kasbonKaryawanService;

	@Autowired
	private StringFormatterFactory stringFormater;

	@FXML
	private ListView<DataKaryawan> listView;
	@FXML
	private TableView<KasbonKaryawan> tableView;
	@FXML
	private TableColumn<KasbonKaryawan, String> columnTanggal;
	@FXML
	private TableColumn<KasbonKaryawan, Double> columnPeminjaman;
	@FXML
	private TableColumn<KasbonKaryawan, Double> columnPembayaran;
	@FXML
	private TableColumn<KasbonKaryawan, Double> columnSaldo;
	@FXML
	private TableColumn<KasbonKaryawan, String> columnWaktu;
	@FXML
	private TextField txtNip;
	@FXML
	private TextField txtNama;
	@FXML
	private TextField txtJabatan;
	@FXML
	private Button btnCetak;

	private void setFields(DataKaryawan karyawan) {
		try {
			txtNip.setText(karyawan.getNip());
			txtNama.setText(karyawan.getNama());
			txtJabatan.setText(karyawan.getJabatan().getNama());
			tableView.getItems().addAll(kasbonKaryawanService.findByKaryawanOrderByCreatedDateAsc(karyawan));
		} catch (Exception e) {
			logger.error("Tidak dapat mendapatkan data kasbon untuk karyawan atas nama {}" + karyawan.getNama(), e);

			StringBuilder sb = new StringBuilder("Tidak dapat mendapatkan informasi kasbon karyawan atas nama ");
			sb.append(karyawan.getNama()).append(" dengan nip ").append(karyawan.getNip());

			ExceptionDialog ex = new ExceptionDialog(e);
			ex.setTitle("Daftar kasbon karyawan");
			ex.setHeaderText(sb.toString());
			ex.setContentText(e.getMessage());
			ex.initModality(Modality.APPLICATION_MODAL);
			ex.show();
		}
	}

	private void clearFields() {
		txtNip.clear();
		txtNama.clear();
		txtJabatan.clear();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tableView.setSelectionModel(null);

		columnTanggal.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<KasbonKaryawan, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<KasbonKaryawan, String> param) {
						KasbonKaryawan kasbon = param.getValue();
						if (kasbon != null) {
							return new SimpleStringProperty(
									stringFormater.getDateIndonesianFormatter(kasbon.getTanggalPinjam().toLocalDate()));
						} else {
							return null;
						}
					}
				});
		columnTanggal
				.setCellFactory(new Callback<TableColumn<KasbonKaryawan, String>, TableCell<KasbonKaryawan, String>>() {

					@Override
					public TableCell<KasbonKaryawan, String> call(TableColumn<KasbonKaryawan, String> param) {
						return new TableCell<KasbonKaryawan, String>() {
							@Override
							protected void updateItem(String item, boolean empty) {
								setAlignment(Pos.CENTER);
								super.updateItem(item, empty);
								if (empty) {
									setText(null);
								} else {
									setText(item);
								}
							}
						};
					}
				});

		columnPembayaran.setCellValueFactory(new PropertyValueFactory<KasbonKaryawan, Double>("pembayaran"));
		columnPembayaran
				.setCellFactory(new Callback<TableColumn<KasbonKaryawan, Double>, TableCell<KasbonKaryawan, Double>>() {

					@Override
					public TableCell<KasbonKaryawan, Double> call(TableColumn<KasbonKaryawan, Double> param) {
						return new TableCell<KasbonKaryawan, Double>() {
							@Override
							protected void updateItem(Double item, boolean empty) {
								super.updateItem(item, empty);
								setAlignment(Pos.CENTER_RIGHT);
								if (empty) {
									setText(null);
								} else {
									setText(stringFormater.getCurrencyFormate(item));
								}
							}
						};
					}
				});

		columnPeminjaman.setCellValueFactory(new PropertyValueFactory<KasbonKaryawan, Double>("pinjaman"));
		columnPeminjaman
				.setCellFactory(new Callback<TableColumn<KasbonKaryawan, Double>, TableCell<KasbonKaryawan, Double>>() {

					@Override
					public TableCell<KasbonKaryawan, Double> call(TableColumn<KasbonKaryawan, Double> param) {
						return new TableCell<KasbonKaryawan, Double>() {
							@Override
							protected void updateItem(Double item, boolean empty) {
								super.updateItem(item, empty);
								setAlignment(Pos.CENTER_RIGHT);
								if (empty) {
									setText(null);
								} else {
									setText(stringFormater.getCurrencyFormate(item));
								}
							}
						};
					}
				});

		columnSaldo.setCellValueFactory(new PropertyValueFactory<KasbonKaryawan, Double>("saldoTerakhir"));
		columnSaldo
				.setCellFactory(new Callback<TableColumn<KasbonKaryawan, Double>, TableCell<KasbonKaryawan, Double>>() {

					@Override
					public TableCell<KasbonKaryawan, Double> call(TableColumn<KasbonKaryawan, Double> param) {
						return new TableCell<KasbonKaryawan, Double>() {
							@Override
							protected void updateItem(Double item, boolean empty) {
								super.updateItem(item, empty);
								setAlignment(Pos.CENTER_RIGHT);
								if (empty) {
									setText(null);
								} else {
									if (item == 0D) {
										setTextFill(Color.GREEN);
									} else {
										setTextFill(Color.RED);
									}
									setText(stringFormater.getCurrencyFormate(item));
								}
							}
						};
					}
				});

		listView.setCellFactory(new Callback<ListView<DataKaryawan>, ListCell<DataKaryawan>>() {

			@Override
			public ListCell<DataKaryawan> call(ListView<DataKaryawan> param) {
				return new ListCell<DataKaryawan>() {
					HBox box;
					Label nip;
					Label nama;

					@Override
					protected void updateItem(DataKaryawan item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							box = new HBox(10);
							nip = new Label(item.getNip());
							nama = new Label(item.getNama());
							box.getChildren().add(nip);
							box.getChildren().add(nama);
							setGraphic(box);
						}
					}
				};
			}
		});
		listView.getSelectionModel().selectedItemProperty().addListener(
				(ObservableValue<? extends DataKaryawan> observable, DataKaryawan oldValue, DataKaryawan newValue) -> {
					tableView.getItems().clear();
					if (newValue != null) {
						setFields(newValue);
					} else {
						clearFields();
					}
				});

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.springContext = applicationContext;
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {

	}

	@Override
	public Node initView() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/scenes/inner/peminjaman/karyawan/List.fxml"));
		loader.setController(springContext.getBean(this.getClass()));
		return loader.load();
	}

	@Override
	public void setStage(Stage stage) {

	}

	@Override
	public void initConstuct() {
		try {
			listView.getItems().clear();
			listView.getItems().addAll(this.serviceKaryawan.findAll());
		} catch (Exception e) {
			logger.error("Tidak dapat memuat data karyawan", e);

			ExceptionDialog ex = new ExceptionDialog(e);
			ex.setTitle("Daftar kasbon karyawan");
			ex.setHeaderText("Tidak dapat memuat daftar data karyawan");
			ex.setContentText(e.getMessage());
			ex.initModality(Modality.APPLICATION_MODAL);
			ex.show();
		}

	}

	@Override
	public void setNotificationDialog(DialogsFX notif) {

	}

	@FXML
	public void doClearSelection(ActionEvent event) {
		this.listView.getSelectionModel().clearSelection();
	}

	@FXML
	public void doRefresh(ActionEvent event) {
		initConstuct();
	}

	@FXML
	public void doPrint(ActionEvent event) {
	}

}
