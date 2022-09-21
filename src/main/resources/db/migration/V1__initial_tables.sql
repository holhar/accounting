create table checking_account_entry
(
  id  bigserial not null,
  account_id varchar(255),
  amount_currency varchar(255),
  amount_in_minor_unit int8,
  bank_code varchar(255),
  booking_date timestamp,
  booking_text varchar(255),
  client varchar(255),
  client_reference varchar(255),
  creditor_id varchar(255),
  customer_reference varchar(255),
  intended_use varchar(1500),
  type varchar(255),
  value_date timestamp,
  primary key (id)
);

create table credit_card_entry 
(
  id  bigserial not null,
  amount_currency varchar(255),
  amount_in_minor_unit int8,
  billed_and_not_included boolean not null,
  description varchar(255),
  receipt_date timestamp,
  type varchar(255),
  value_date timestamp,
  primary key (id)
);

create table accounting_schema.monthly_report
(
  id  bigserial not null,
  expenditure_currency varchar(255),
  expenditure_in_minor_unit int8,
  friendly_name varchar(255),
  income_currency varchar(255),
  income_in_minor_unit int8,
  investment_currency varchar(255),
  investment_in_minor_unit int8,
  month int4,
  saving_rate numeric(19, 2),
  win_currency varchar(255),
  win_in_minor_unit int8,
  year int4 not null,
  primary key (id)
);

create table monthly_report_cost_centres 
(
  monthly_report_id int8 not null,
  amount bytea,
  entry_type varchar(255)
);

alter table monthly_report_cost_centres 
  add constraint FK2s46u9lu0rkeu93vrcugjidal foreign key (monthly_report_id) references monthly_report;
