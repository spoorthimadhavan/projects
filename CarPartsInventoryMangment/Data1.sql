USE [APIv2]
GO
/****** Object:  Table [dbo].[Store]    Script Date: 04/30/2014 22:08:24 ******/
SET IDENTITY_INSERT [dbo].[Store] ON
INSERT [dbo].[Store] ([Id], [Name], [Type]) VALUES (1, N'Shop', N'Main Storage')
INSERT [dbo].[Store] ([Id], [Name], [Type]) VALUES (2, N'Warehouse1', N'Secondary Storage')
INSERT [dbo].[Store] ([Id], [Name], [Type]) VALUES (3, N'Warehouse2', N'Secondary Storage')
SET IDENTITY_INSERT [dbo].[Store] OFF
/****** Object:  Table [dbo].[Expenses]    Script Date: 04/30/2014 22:08:24 ******/
SET IDENTITY_INSERT [dbo].[Expenses] ON
INSERT [dbo].[Expenses] ([Id], [Name], [Amount], [Date]) VALUES (1, N'Maitainence', 1000.0000, CAST(0xA30F0544 AS SmallDateTime))
INSERT [dbo].[Expenses] ([Id], [Name], [Amount], [Date]) VALUES (2, N'Shahrukh', 5000.0000, CAST(0xA30F0549 AS SmallDateTime))
SET IDENTITY_INSERT [dbo].[Expenses] OFF
/****** Object:  Table [dbo].[Party]    Script Date: 04/30/2014 22:08:24 ******/
SET IDENTITY_INSERT [dbo].[Party] ON
INSERT [dbo].[Party] ([Name], [Location], [Id]) VALUES (N'Nisar and Sons', N'Karimabad', 1)
INSERT [dbo].[Party] ([Name], [Location], [Id]) VALUES (N'Ehsan Autos', N'Mukka Chowk', 2)
SET IDENTITY_INSERT [dbo].[Party] OFF
/****** Object:  Table [dbo].[Parts]    Script Date: 04/30/2014 22:08:24 ******/
INSERT [dbo].[Parts] ([PartNo], [Name], [Model], [Car ], [CostPrice]) VALUES (N'bmp-123', N'Bumper', N'2007', N'Alto', 6000.0000)
INSERT [dbo].[Parts] ([PartNo], [Name], [Model], [Car ], [CostPrice]) VALUES (N'bmp-125', N'Bumper', N'2007', N'Cultus', 56.0000)
/****** Object:  Table [dbo].[Inventory]    Script Date: 04/30/2014 22:08:24 ******/
INSERT [dbo].[Inventory] ([PartNo], [Qty], [StoreId]) VALUES (N'bmp-123', 250, 1)
INSERT [dbo].[Inventory] ([PartNo], [Qty], [StoreId]) VALUES (N'bmp-125', 640, 1)
INSERT [dbo].[Inventory] ([PartNo], [Qty], [StoreId]) VALUES (N'bmp-125', 0, 2)
INSERT [dbo].[Inventory] ([PartNo], [Qty], [StoreId]) VALUES (N'bmp-125', 10, 3)
/****** Object:  Table [dbo].[Debt]    Script Date: 04/30/2014 22:08:24 ******/
SET IDENTITY_INSERT [dbo].[Debt] ON
INSERT [dbo].[Debt] ([DebtId], [DealDate], [Total], [Payed], [LastPaymentDate], [PartyId]) VALUES (12, CAST(0xA30402FA AS SmallDateTime), 5000.0000, 4000.0000, CAST(0xA30402FA AS SmallDateTime), 1)
INSERT [dbo].[Debt] ([DebtId], [DealDate], [Total], [Payed], [LastPaymentDate], [PartyId]) VALUES (14, CAST(0xA3190586 AS SmallDateTime), 366800.0000, 301400.0000, CAST(0xA31A0212 AS SmallDateTime), 1)
SET IDENTITY_INSERT [dbo].[Debt] OFF
/****** Object:  Table [dbo].[Sales]    Script Date: 04/30/2014 22:08:24 ******/
SET IDENTITY_INSERT [dbo].[Sales] ON
INSERT [dbo].[Sales] ([PartNo], [SellingPrice], [DateSold], [CustomerType], [PartyId], [Qty], [Id]) VALUES (N'bmp-123', 7000.0000, CAST(0xA3100000 AS SmallDateTime), N'Party', 1, 200, 1)
INSERT [dbo].[Sales] ([PartNo], [SellingPrice], [DateSold], [CustomerType], [PartyId], [Qty], [Id]) VALUES (N'bmp-123', 8000.0000, CAST(0xA3100000 AS SmallDateTime), N'Party', 1, 200, 2)
INSERT [dbo].[Sales] ([PartNo], [SellingPrice], [DateSold], [CustomerType], [PartyId], [Qty], [Id]) VALUES (N'bmp-125', 80.0000, CAST(0xA3100459 AS SmallDateTime), N'Party', 1, 80, 5)
SET IDENTITY_INSERT [dbo].[Sales] OFF
/****** Object:  Table [dbo].[Purchase]    Script Date: 04/30/2014 22:08:24 ******/
SET IDENTITY_INSERT [dbo].[Purchase] ON
INSERT [dbo].[Purchase] ([SellingPrice], [DatePurchased], [PartyId], [Qty], [PartNo], [Id]) VALUES (600.0000, CAST(0xA310042C AS SmallDateTime), 1, 500, N'bmp-123', 1)
SET IDENTITY_INSERT [dbo].[Purchase] OFF
/****** Object:  Table [dbo].[DebtDetails]    Script Date: 04/30/2014 22:08:34 ******/
SET IDENTITY_INSERT [dbo].[DebtDetails] ON
INSERT [dbo].[DebtDetails] ([Id], [Qty], [SellPrice], [PartNo], [DebtId]) VALUES (5, 100, 58.0000, N'bmp-125', 14)
INSERT [dbo].[DebtDetails] ([Id], [Qty], [SellPrice], [PartNo], [DebtId]) VALUES (6, 50, 7220.0000, N'bmp-123', 14)
SET IDENTITY_INSERT [dbo].[DebtDetails] OFF
