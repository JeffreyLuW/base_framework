function formatJson(filterVal, jsonData) {
  return jsonData.map(v =>
    filterVal.map(j => {
      if (j === "qdsj") {
        return v[j].substring(0, 10);
      } else {
        return v[j];
      }
    })
  );
}
export function ex(multiHeader, header, filterVal, list, filename) {
  let mHeader = multiHeader,
    h = header,
    fval = filterVal,
    l = list,
    fname = filename;
  import("./Export2Excel").then(excel => {
    const multiHeader = [mHeader];
    const header = h;
    const filterVal = fval;
    const list = l;

    const data = formatJson(filterVal, list);
    data.unshift([]);
    const merges = [
      "A1:A2",
      "B1:B2",
      "C1:C2",
      "D1:D2",
      "E1:E2",
      "F1:F2",
      "G1:G2",
      "H1:H2",
      "I1:I2"
    ];
    const filename = fname;
    excel.export_json_to_excel({
      header,
      merges,
      data,
      filename,
      autoWidth: true
    });
  });
}
