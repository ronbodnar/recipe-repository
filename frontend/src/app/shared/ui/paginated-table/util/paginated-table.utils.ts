/**
 * Binds all input values to property values from the row data.
 *
 * String values (value) are used to map row data from the table into the component in that row.
 * Keys that are not valid in the table's row data are ignored and the original value is used.
 *
 * To map the whole row data, use the value "*"
 *
 * To map properties, wrap them in {curly braces}, like this:
 *      INPUT:   '/contracts/{prefix}-{sequence}'
 *      OUTPUT:  '/contracts/MHA-130'
 * In this example, the table row would have the properties { prefix: 'MHA', sequence: 130 }
 *
 * @param value The value to bind properties to.
 * @param rowData An object containing the table row data.
 * @returns The value with placeholders replaced with the bound values.
 */
export function bindValue(value: any, rowData: any): any {
  if (!value) {
    return value;
  }

  /*     console.log(
      'Binding value',
      value,
      'of type',
      typeof value,
      'with row data',
      rowData
    ); */

  if (Array.isArray(value)) {
    const valueCopy = [...value] as { [key: string]: string }[];
    valueCopy.forEach((item: any, index: number) => (valueCopy[index] = bindValue(item, rowData)));
    return valueCopy;
  }

  if (typeof value === 'object') {
    const valueCopy = { ...value } as { [key: string]: any };
    Object.entries(valueCopy).forEach(([k, v]) => (valueCopy[k] = bindValue(v, rowData)));
    return valueCopy;
  }

  if (typeof value !== 'string') {
    return value;
  }

  let boundValue: any = String(value);
  if (value === '*') {
    boundValue = rowData;
  } else if (value.includes('{') && value.includes('}')) {
    const matches = value.match(/({[\w.]+})/g);
    matches?.forEach((match) => {
      const cleanMatch = match.replace(/[{}]/g, '');
      const matchedValue = getPropertyValue(rowData, cleanMatch);

      if (matchedValue === undefined) {
        boundValue = '';
        return;
      }

      if (matches.length > 1 || match.length !== value.length) {
        // Keep it a string when replacing multiple placeholders or partial matches
        boundValue = boundValue.replace(match, String(matchedValue));
      } else {
        // If it's a full match, assign the actual value (keeps number/boolean types)
        boundValue = matchedValue;
      }
    });
  }
  return boundValue;
}

export function getPropertyValue(obj: any, path: string): any {
  if (!obj || !path) {
    console.error('Invalid arguments for getNestedValue', path, obj);
    return undefined;
  }
  let colValue = obj[path];

  if (path.includes('.')) {
    const nestedProperty = path.split('.');

    let property = undefined;
    for (let i = 0; i < nestedProperty.length; i++) {
      if (!property) {
        property = obj[nestedProperty[i]];
        continue;
      }
      colValue = property[nestedProperty[i]];
    }
  }
  return colValue;
}
