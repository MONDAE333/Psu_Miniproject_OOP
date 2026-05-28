# PSU Miniproject OOP - POS System (Text-Based)

## 📋 รายละเอียด Project
โปรแกรม POS (Point of Sale) แบบ Text-Based ที่เป็นการจำลองระบบการขายของร้าน **Moshi Moshi** 
โปรเจคนี้ออกแบบมาเพื่อฝึกการพัฒนาระบบตามหลักการ Object-Oriented Programming (OOP) 
โดยเน้นการแยก Class ที่เหมาะสมและการสืบทอด (Inheritance) ให้ถูกต้อง

> **หมายเหตุ:** โปรแกรมนี้เป็นเวอร์ชั่นจำลองอย่างง่าย ไม่ได้ใช้ข้อมูลระบบจริงของร้าน 100%

## 🎯 วัตถุประสงค์
- ✅ ฝึกการออกแบบระบบตามหลักการ OOP
- ✅ เรียนรู้การแยก Responsibility ของแต่ละ Class
- ✅ ประยุกต์ใช้ Inheritance (สืบทอด) อย่างถูกต้อง
- ✅ พัฒนาทักษะการจัดการข้อมูลและสถานะของระบบ

## 🛠️ เทคโนโลยี
- **ภาษา:** Java
- **ประเภท:** Console Application (Text-Based)
- **ฐานข้อมูล:** JSON File
- **หลักการหลัก:** Object-Oriented Programming (OOP)

## 📁 โครงสร้าง Project

```
Psu_Miniproject_OOP/
├── src/
│   ├── Main.java              # Entry point ของโปรแกรม
│   ├── Product.java           # Class สำหรับจัดการข้อมูลสินค้า
│   ├── Bill.java              # Class สำหรับจัดการบิล/ใบเสร็จ
│   ├── MenuItem.java          # Class สำหรับรายการเมนู
│   ├── Order.java             # Class สำหรับจัดการคำสั่งซื้อ
│   ├── FileManager.java       # Class สำหรับการอ่าน/เขียน JSON
│   └── ... (อื่น ๆ)
├── data/
│   ├── products.json          # ไฟล์ JSON เก็บข้อมูลสินค้า
│   ├── bills.json             # ไฟล์ JSON เก็บข้อมูลบิล/ใบเสร็จ
│   └── ... (ไฟล์ JSON อื่น)
├── README.md
└── ...
```

## 🚀 วิธีการใช้งาน

### ข้อกำหนดเบื้องต้น
- Java Development Kit (JDK) 8 หรือเวอร์ชั่นที่สูงกว่า
- ไลบรารี่ JSON (เช่น Gson, JSONObject หรือ jackson)

### การรันโปรแกรม

```bash
# 1. ไปที่ directory ของ project
cd Psu_Miniproject_OOP

# 2. คอมไพล์ไฟล์ Java ทั้งหมด
javac src/*.java

# 3. รัน Main Program
java -cp src Main
```

## 💡 ฟีเจอร์หลัก

### 1. **การจัดการสินค้า (Product Management)**
   - เพิ่ม/ลบ/แก้ไขสินค้า
   - จัดเก็บข้อมูลสินค้า (ชื่อ, ราคา, จำนวน) ในไฟล์ JSON

### 2. **การสร้างบิล (Bill Generation)**
   - สร้างใบเสร็จจากสินค้าที่เลือก
   - คำนวณราคารวม ส่วนลด ภาษี
   - บันทึกข้อมูลลงไฟล์ JSON

### 3. **ระบบคำสั่งซื้อ (Order System)**
   - รับ input จากผู้ใช้
   - ประมวลผลคำสั่ง
   - แสดงผลการทำรายการ

### 4. **เมนูหลัก (Main Menu)**
   - ดูรายการสินค้า
   - สร้างใบเสร็จใหม่
   - ดูประวัติการขาย
   - ออกจากโปรแกรม

## 📊 ตัวอย่างโครงสร้าง JSON

### products.json
```json
{
  "products": [
    {
      "id": "001",
      "name": "Care Bears",
      "price": 120.00,
      "quantity": 50
    },
    {
      "id": "002",
      "name": "Moshi Pen",
      "price": 110.00,
      "quantity": 45
    },
    {
      "id": "003",
      "name": "Sunscreen",
      "price": 30.00,
      "quantity": 100
    }
  ]
}
```

### bills.json
```json
{
  "bills": [
    {
      "billId": "BILL001",
      "date": "2026-05-28",
      "items": [
        {
          "productId": "001",
          "productName": "Care Bears",
          "quantity": 2,
          "price": 120.00,
          "total": 240.00
        }
      ],
      "subtotal": 240.00,
      "tax": 19.20,
      "total": 259.20
    }
  ]
}
```

## 🏗️ สถาปัตยกรรม OOP

โปรเจคนี้ประยุกต์ใช้หลักการ OOP ต่อไปนี้:

- **Encapsulation** - ซ่อน data ที่ไม่จำเป็น ใช้ getter/setter
- **Inheritance** - สืบทอด class เพื่อนำกลับมาใช้อีก
- **Polymorphism** - override method สำหรับพฤติกรรมที่ต่างกัน
- **Abstraction** - สร้าง abstract class หรือ interface เมื่อจำเป็น

## 📝 การจัดการไฟล์ JSON

### การอ่านข้อมูล
```java
// ตัวอย่างการอ่านข้อมูลจากไฟล์ JSON
FileManager.loadProducts("data/products.json");
```

### การบันทึกข้อมูล
```java
// ตัวอย่างการบันทึกข้อมูลลงไฟล์ JSON
FileManager.saveProducts("data/products.json", products);
```

## 👨‍💻 ผู้พัฒนา
- [MONDAE333](https://github.com/MONDAE333)

## 📅 ข้อมูลโปรเจค
- **สร้าง:** 15 มีนาคม 2026
- **แก้ไขครั้งล่าสุด:** 28 พฤษภาคม 2026
- **ประเทศ:** ประเทศไทย (Moshi Moshi)

## 📚 เนื้อหาที่เรียนรู้

ในโปรเจคนี้ คุณจะได้เรียนรู้เกี่ยวกับ:

- การออกแบบ Class Structure
- การจัดการ Object และ Instance
- การใช้ Constructor และ Initialization
- Getter/Setter Methods
- Class Inheritance และ super keyword
- Method Overriding
- Collection (ArrayList, HashMap) สำหรับจัดเก็บข้อมูล
- Exception Handling เบื้องต้น
- **การอ่าน/เขียน JSON Files**
- **Serialization และ Deserialization**

## 🎓 หมายเหตุ

โปรเจคนี้เป็นส่วนหนึ่งของการเรียนรู้ OOP ระดับพื้นฐาน 
โดยเพิ่มการจัดการข้อมูลผ่านไฟล์ JSON เพื่อให้เข้าใจการสถาปัตยกรรมฐานข้อมูลแบบเรียบง่าย

ถ้าต้องการพัฒนาต่อ สามารถเพิ่มฟีเจอร์ต่อไปนี้ได้:

- 🔐 ระบบเข้าสู่ระบบ (Authentication)
- 📊 ระบบรายงาน (Report Generation)
- 🗄️ ฐานข้อมูล SQL (Database Integration)
- 🎨 GUI Interface (Graphical User Interface)

---
