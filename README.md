# Jvm Reverse Shell

A proof-of-concept demonstration of unsafe Jvm object deserialization (CVE-2015-6420).

This repository contains three standalone projects:

- `payload-generator`, our program that generates a serial binary `payload.sbin` containing the serialized malicious object (reverse shell backend). The file is ready-to-deploy
- `victim`, our victim server. Vulnerable to CVE-2015-6420
- `c2`, our command & control server containing the reverse shell frontend

**Note:** This is part of a research project for my university; Code is not fully documented. I might add some slides later on.

See [ysoserial](https://github.com/frohoff/ysoserial) (esp. [CommonsCollections2](https://github.com/frohoff/ysoserial/blob/master/src/main/java/ysoserial/payloads/CommonsCollections2.java)) for some detailed insight on how this exploit works.
